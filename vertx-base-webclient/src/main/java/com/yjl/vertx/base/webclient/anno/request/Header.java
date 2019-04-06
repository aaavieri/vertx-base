package com.yjl.vertx.base.webclient.anno.request;

import java.lang.annotation.*;

@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Header {
	String key() default "";
	String value() default "";
	String paramKey() default "";
}
