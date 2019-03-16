package com.yjl.vertx.base.com.factory.component;

import io.vertx.core.spi.logging.LogDelegateFactory;

public abstract class BaseLogFactory extends BaseAnnotationComponentFactory {
	@Override
	public void configure() {
		System.setProperty("vertx.logger-delegate-factory-class-name", this.getDelegateFactoryClass().getName());
	}

	protected abstract Class<? extends LogDelegateFactory> getDelegateFactoryClass();
}
