package com.yjl.vertx.base.dao.anno.operation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Select {
	String value();
	boolean single() default false;
}
