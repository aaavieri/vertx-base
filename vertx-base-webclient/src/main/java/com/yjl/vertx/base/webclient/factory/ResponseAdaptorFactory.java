package com.yjl.vertx.base.webclient.factory;

import com.yjl.vertx.base.com.factory.component.SimpleSetFactory;
import com.yjl.vertx.base.webclient.adaptor.AbstractResponseAdaptor;

public class ResponseAdaptorFactory extends SimpleSetFactory<AbstractResponseAdaptor> {

	@Override
	protected Class<AbstractResponseAdaptor> getAbstractParentClass() {
		return AbstractResponseAdaptor.class;
	}
}
