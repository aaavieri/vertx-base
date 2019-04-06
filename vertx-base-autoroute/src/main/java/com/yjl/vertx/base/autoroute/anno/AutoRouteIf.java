package com.yjl.vertx.base.autoroute.anno;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AutoRouteIf {
	String value() default "";
}
