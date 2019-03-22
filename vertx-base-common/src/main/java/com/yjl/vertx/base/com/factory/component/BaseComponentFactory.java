package com.yjl.vertx.base.com.factory.component;

import com.google.inject.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseComponentFactory extends AbstractModule {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public void stop() {}

	public void beforeConfigure() {}

	public abstract void configure();

	public void afterConfigure() {}

	protected Logger getLogger() {
		return this.logger;
	}
}
