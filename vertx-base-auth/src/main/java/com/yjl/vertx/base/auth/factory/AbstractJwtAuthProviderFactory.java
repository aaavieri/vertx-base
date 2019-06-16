package com.yjl.vertx.base.auth.factory;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

public abstract class AbstractJwtAuthProviderFactory extends BaseAnnotationComponentFactory {
    
    @Inject
    private Vertx vertx;
    
    @Override
    public void configure() {
        JWTAuth provider = JWTAuth.create(this.vertx, this.getJwtAuthOptions());
        this.bind(JWTAuth.class).toInstance(provider);
    }
    
    protected abstract JWTAuthOptions getJwtAuthOptions();
}
