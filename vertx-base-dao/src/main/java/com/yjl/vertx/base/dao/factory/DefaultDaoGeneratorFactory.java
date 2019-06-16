package com.yjl.vertx.base.dao.factory;

import com.google.inject.name.Names;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.generator.ProxyGeneratorIf;
import com.yjl.vertx.base.dao.generator.DefaultDaoGenerator;

public class DefaultDaoGeneratorFactory extends BaseAnnotationComponentFactory {
    @Override
    public void configure() {
        this.bind(ProxyGeneratorIf.class).annotatedWith(Names.named("defaultDaoGenerator"))
            .to(DefaultDaoGenerator.class).asEagerSingleton();
    }
}
