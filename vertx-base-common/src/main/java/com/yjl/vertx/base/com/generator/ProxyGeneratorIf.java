package com.yjl.vertx.base.com.generator;

public interface ProxyGeneratorIf {
    <T> T getProxyInstance(Class<T> clientIf);
}
