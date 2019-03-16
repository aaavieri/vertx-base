package com.yjl.vertx.base.com.anno.component;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@BindingAnnotation
public @interface Config {
	String value() default "";
}
