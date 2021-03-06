package com.yjl.vertx.base.web.anno.component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestRouteV1Handler {

    String value() default "";
}
