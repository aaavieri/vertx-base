package com.yjl.vertx.base.webclient.factory;

import com.yjl.vertx.base.com.factory.component.SimpleSetFactory;
import com.yjl.vertx.base.webclient.executor.AbstractRequestExecutor;

public class RequestExecutorFactory extends SimpleSetFactory<AbstractRequestExecutor> {

	@Override
	protected Class<AbstractRequestExecutor> getAbstractParentClass() {
		return AbstractRequestExecutor.class;
	}
}
