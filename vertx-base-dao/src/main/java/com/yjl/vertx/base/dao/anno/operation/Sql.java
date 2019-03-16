package com.yjl.vertx.base.dao.anno.operation;

import com.yjl.vertx.base.dao.enumeration.SqlOperation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Sql {

	String value();

	SqlOperation operation() default SqlOperation.SELECT;
}
