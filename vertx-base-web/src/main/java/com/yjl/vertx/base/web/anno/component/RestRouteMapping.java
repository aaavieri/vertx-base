package com.yjl.vertx.base.web.anno.component;

import com.yjl.vertx.base.web.enumeration.RouteMethod;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestRouteMapping {

    String value() default "";

    RouteMethod method() default RouteMethod.GET;

    String descript() default "";

    boolean autoHandleError() default true;

    boolean regexp() default false;
}
