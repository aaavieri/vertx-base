package com.yjl.vertx.base.web.anno.handler;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
@Documented
public @interface RestV1HandlerInject {
	String value() default "";
}
