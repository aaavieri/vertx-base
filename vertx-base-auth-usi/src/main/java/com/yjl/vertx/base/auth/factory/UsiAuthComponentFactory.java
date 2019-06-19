package com.yjl.vertx.base.auth.factory;

import com.google.inject.Inject;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.yjl.vertx.base.auth.component.*;
import com.yjl.vertx.base.auth.mapper.AccountMapper;
import com.yjl.vertx.base.auth.mapper.MenuInfoMapper;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.generator.ProxyGeneratorIf;

import java.util.stream.Stream;

public class UsiAuthComponentFactory extends BaseAnnotationComponentFactory {
    
    @Inject
    @Named("defaultDaoGenerator")
    private ProxyGeneratorIf defaultDaoGenerator;
    
    @Override
    public void configure() {
        this.bind(AccountMapper.class).toInstance(this.defaultDaoGenerator.getProxyInstance(AccountMapper.class));
        this.bind(MenuInfoMapper.class).toInstance(this.defaultDaoGenerator.getProxyInstance(MenuInfoMapper.class));
        this.bind(AuthenticationComponentIf.class).to(UsiAuthenticationComponent.class).asEagerSingleton();
        this.bind(AuthorizeComponentIf.class).to(UsiAuthorizeComponent.class).asEagerSingleton();
        Multibinder<AuthenticationCompleteListener> authenticationBinder = Multibinder.newSetBinder(this.binder(), AuthenticationCompleteListener.class);
        Stream.of(UsiTokenGeneratorComponent.class, UsiAuthenticationSucceedComponent.class)
            .peek(clazz -> this.bind(clazz).asEagerSingleton())
            .forEach(clazz -> authenticationBinder.addBinding().to(clazz));
        Multibinder<AuthorizeCompleteListener> authorizeBinder = Multibinder.newSetBinder(this.binder(), AuthorizeCompleteListener.class);
        Stream.of(AuthorizeCompleteFailComponent.class)
            .peek(clazz -> this.bind(clazz).asEagerSingleton())
            .forEach(clazz -> authorizeBinder.addBinding().to(clazz));
    }
}