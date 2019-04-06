package com.yjl.vertx.base.webclient.factory;

import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.webclient.context.WebClientContextCache;

public class WebClientContextCacheFactory extends BaseAnnotationComponentFactory {
	@Override
	public void configure() {
		this.bind(WebClientContextCache.class).asEagerSingleton();
	}
}
