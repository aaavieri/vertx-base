package com.yjl.vertx.base.autoroute.anno;

import io.vertx.core.http.HttpMethod;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AutoRouteIfMethod {
	String value() default "";
	HttpMethod route() default HttpMethod.GET;
}
