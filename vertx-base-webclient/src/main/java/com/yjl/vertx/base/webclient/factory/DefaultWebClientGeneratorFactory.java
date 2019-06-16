package com.yjl.vertx.base.webclient.factory;

import com.google.inject.name.Names;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.generator.ProxyGeneratorIf;
import com.yjl.vertx.base.webclient.generator.DefaultWebClientGenerator;

public class DefaultWebClientGeneratorFactory extends BaseAnnotationComponentFactory {
    
    @Override
    public void configure() {
        this.bind(ProxyGeneratorIf.class).annotatedWith(Names.named("webClientGenerator"))
            .to(DefaultWebClientGenerator.class).asEagerSingleton();
    }
}
