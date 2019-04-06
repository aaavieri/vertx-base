package com.yjl.vertx.base.webclient.anno.request;

import java.lang.annotation.*;

@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(MultiWebOptions.class)
public @interface WebOptions {
	String key();
	String value();
}
