package com.yjl.vertx.base.com.factory.component;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.StatusPrinter;
import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.component.Config;
import io.vertx.core.json.JsonObject;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static ch.qos.logback.core.spi.FilterReply.ACCEPT;
import static ch.qos.logback.core.spi.FilterReply.DENY;

public class SimpleSlf4jLogbackFactory extends BaseSlf4jFactory {

	@Inject(optional = true)
	@Config("logbackConfig.level.root")
	private String rootLogLevel = "INFO";

	@Inject(optional = true)
	@Config("logbackConfig.file")
	private String logFileName = "./vertx.log";

	@Inject(optional = true)
	@Config("logbackConfig.maxFileSize")
	private String maxFileSize = "128MB";

	@Inject(optional = true)
	@Config("logbackConfig.maxHistory")
	private int maxHistory = 15;

	@Inject(optional = true)
	@Config("logbackConfig.totalSize")
	private String totalSize = "32GB";

	@Inject(optional = true)
	@Config("logbackConfig.level")
	private JsonObject customLogLevels = new JsonObject();

	@Override
	protected void initLogAdaptor() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger rootLogger = loggerContext.getLogger("root");
		List<Appender<ILoggingEvent>> appenderList = new ArrayList<>();
		rootLogger.iteratorForAppenders().forEachRemaining(appenderList::add);
		loggerContext.reset();

		RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
		fileAppender.setContext(loggerContext);
		fileAppender.setName("file");
		fileAppender.setFile(this.logFileName);
		fileAppender.setAppend(true);
		fileAppender.setPrudent(false);

		Level level = Level.valueOf(this.rootLogLevel);
		LevelFilter levelFilter = new LevelFilter();
		levelFilter.setLevel(level);
		levelFilter.setOnMatch(ACCEPT);
		levelFilter.setOnMismatch(DENY);
		levelFilter.start();
		fileAppender.addFilter(levelFilter);

		SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy();
		policy.setMaxFileSize(FileSize.valueOf(this.maxFileSize));
		policy.setFileNamePattern(logFileName + ".%d{yyyy-MM-dd}.%i.log");
		policy.setMaxHistory(this.maxHistory);
		policy.setTotalSizeCap(FileSize.valueOf(this.totalSize));
		policy.setParent(fileAppender);
		policy.setContext(loggerContext);
		policy.start();

		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(loggerContext);
		encoder.setPattern("%d %p (%file:%line\\)- %m%n");
		encoder.start();

		fileAppender.setRollingPolicy(policy);
		fileAppender.setEncoder(encoder);

		appenderList.add(fileAppender);
		appenderList.stream().peek(Appender::start).forEach(rootLogger::addAppender);

		this.customLogLevels.fieldNames().stream().filter(fieldName -> !"ROOT".equalsIgnoreCase(fieldName)).forEach(customLogLevel -> {
			Logger customLogger = loggerContext.getLogger(customLogLevel);
			customLogger.setAdditive(false);
			appenderList.forEach(customLogger::addAppender);
			customLogger.setLevel(Level.toLevel(this.customLogLevels.getString(customLogLevel), customLogger.getLevel()));
		});
		StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
	}
}
