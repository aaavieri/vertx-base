package com.yjl.vertx.base.webclient.anno.request;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(MultiQueryParam.class)
public @interface QueryParam {
	String key();
	String paramKey();
}
