package com.yjl.vertx.base.auth.factory;

import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.redis.factory.RedisFutureComponentFactory;

@ComponentInitializer(factoryClass = UsiAuthComponentFactory.class)
@ComponentInitializer(factoryClass = PublicKeyJwtAuthProviderFactory.class)
@ComponentInitializer(factoryClass = RedisFutureComponentFactory.class)
public class UsiAuthorizerFactory extends AuthorizerFactory {
}
