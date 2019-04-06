package com.yjl.vertx.base.web.anno.component;

import io.vertx.core.http.HttpMethod;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestRouteMapping {

    String value() default "";

    HttpMethod method() default HttpMethod.GET;

    String descript() default "";

    boolean autoHandleError() default true;

    boolean regexp() default false;
}
