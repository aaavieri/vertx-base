package com.yjl.vertx.base.dao.anno.operation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface SqlParam {
	String value();
}
