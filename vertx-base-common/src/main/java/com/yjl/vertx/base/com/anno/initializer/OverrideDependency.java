package com.yjl.vertx.base.com.anno.initializer;

import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(MultiOverrideDependency.class)
public @interface OverrideDependency {

	ComponentInitializer value();

	ComponentInitializer[] customInclude() default {};

	Class<? extends BaseAnnotationComponentFactory>[] customExclude() default {};

	ComponentInitializer[] customAll() default {};

	boolean dependNothing() default false;
}
