package com.yjl.vertx.base.com.factory.component;

import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.core.spi.logging.LogDelegateFactory;

public abstract class BaseSlf4jFactory extends BaseLogFactory {

	@Override
	public Class<? extends LogDelegateFactory> getDelegateFactoryClass() {
		this.initLogAdaptor();
		return SLF4JLogDelegateFactory.class;
	}

	protected abstract void initLogAdaptor();
}
