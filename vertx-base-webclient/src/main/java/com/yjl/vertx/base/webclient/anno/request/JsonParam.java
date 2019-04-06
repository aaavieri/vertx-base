package com.yjl.vertx.base.webclient.anno.request;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(value = MultiJsonParam.class)
public @interface JsonParam {
	String key();
	String paramKey();
}
