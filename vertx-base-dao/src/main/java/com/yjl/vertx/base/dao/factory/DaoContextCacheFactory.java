package com.yjl.vertx.base.dao.factory;

import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.dao.context.DaoContextCache;

public class DaoContextCacheFactory extends BaseAnnotationComponentFactory {

	@Override
	public void configure() {
		this.bind(DaoContextCache.class).asEagerSingleton();
	}
}
