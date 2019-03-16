package com.yjl.vertx.base.com.function;

@FunctionalInterface
public interface ThreeParamFunction<R, S, T, U> {

	U apply(R r, S s, T t);
}
