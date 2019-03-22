package com.yjl.vertx.base.com.anno.initializer;

import com.yjl.vertx.base.com.factory.component.BaseComponentFactory;
import com.yjl.vertx.base.com.factory.component.CommonComponentFactory;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(MultiComponentInitializer.class)
public @interface ComponentInitializer {

	// factory class definition
	Class<? extends BaseComponentFactory> factoryClass() default CommonComponentFactory.class;

	// base packages to be scan
	String[] value() default {};

	boolean singleton() default true;
}
