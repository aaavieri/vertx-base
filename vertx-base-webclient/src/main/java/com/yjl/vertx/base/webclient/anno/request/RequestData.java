package com.yjl.vertx.base.webclient.anno.request;

import com.yjl.vertx.base.webclient.enumeration.RequestDataType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(MultiRequestData.class)
public @interface RequestData {
	RequestDataType type();
	String key();
	String value() default "";
	String paramKey() default "";
	boolean whole() default false;
}
