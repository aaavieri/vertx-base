package com.yjl.vertx.base.redis.factory;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.component.ComponentScanner;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;

public class RedisFutureComponentFactory extends BaseAnnotationComponentFactory {
    
    @Inject
    private ComponentScanner componentScanner;
    
    @Override
    public void configure() {
        this.componentScanner.getComponents("com.yjl.vertx.base.redis")
            .forEach(clazz -> this.bind(clazz).asEagerSingleton());
    }
}
