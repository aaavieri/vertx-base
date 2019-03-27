package com.yjl.vertx.base.dao.factory.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.dao.adaptor.AbstractAdaptor;
import com.yjl.vertx.base.dao.anno.component.Dao;
import com.yjl.vertx.base.dao.command.SqlCommand;
import com.yjl.vertx.base.dao.command.SqlCommandBuilder;
import com.yjl.vertx.base.dao.executor.AbstractCommandExecutor;
import io.vertx.core.Future;
import io.vertx.ext.sql.SQLClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@ComponentInitializer(factoryClass = MysqlSqlClientFactory.class)
@ComponentInitializer(factoryClass = AdaptorFactory.class, value = "com.yjl.vertx.base.dao.adaptor")
@ComponentInitializer(factoryClass = SqlExecutorFactory.class, value = "com.yjl.vertx.base.dao.executor")
public class DaoFactory extends BaseAnnotationComponentFactory {

	@Inject
	private SQLClient sqlClient;

	@Inject
	private Set<AbstractAdaptor> adaptors;

	@Inject
	private Set<AbstractCommandExecutor> commandExecutors;

	private Map<Method, AbstractAdaptor> adaptorMap = new HashMap<>();

	private Map<Method, AbstractCommandExecutor> executorMap = new HashMap<>();

	private Map<Method, SqlCommandBuilder> builderMap = new HashMap<>();

	@Override
	public void configure() {
		Stream.of(this.metaData.value()).flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName, Dao.class).stream())
			.filter(clazz -> {
				if (!clazz.isInterface()) {
					this.getLogger().warn("warning: {} is not interface, skipped", clazz.getName());
					return false;
				}
				return true;
			})
			.forEach(clazz -> this.bind(clazz).toInstance(ReflectionsUtil.autoCast(this.getProxyInstance(clazz))));
	}

	private <T> T getProxyInstance(Class<T> daoIf) {
		InvocationHandler invocationHandler = (proxy, method, args) -> {
			if (!this.builderMap.containsKey(method)) {
				this.builderMap.put(method, SqlCommandBuilder.newInstance(method));
			}

			SqlCommand command = this.builderMap.get(method).build(args);
			if (!this.executorMap.containsKey(method)) {
				AbstractCommandExecutor executor = this.commandExecutors.stream().filter(commandExecutor -> commandExecutor.isMatch(command))
					.findFirst().orElseThrow(() -> new FrameworkException().message("can not find executor for:" + method.getName()));
				this.executorMap.put(method, executor);
			}
			if (!this.adaptorMap.containsKey(method)) {
				AbstractAdaptor executor = this.adaptors.stream().filter(adaptor -> adaptor.isMatch(command))
					.findFirst().orElseThrow(() -> new FrameworkException().message("can not find adaptor for:" + method.getName()));
				this.adaptorMap.put(method, executor);
			}
			Future<Object> future = Future.future();
			this.sqlClient.getConnection(as -> {
				if (as.succeeded()) {
					Future<?> executeFuture = this.executorMap.get(method).execute(as.result(), command);
					executeFuture.setHandler(executeResult -> {
						as.result().close();
						if (executeResult.succeeded()) {
							Object adaptResult = this.adaptorMap.get(method).adapt(executeResult.result());
							future.complete(adaptResult);
						} else {
							future.fail(executeResult.cause());
						}
					});
				} else {
					future.fail(as.cause());
				}
			});
			return future;
		};
		return ReflectionsUtil.autoCast(Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { daoIf },
			invocationHandler));
	}
}
