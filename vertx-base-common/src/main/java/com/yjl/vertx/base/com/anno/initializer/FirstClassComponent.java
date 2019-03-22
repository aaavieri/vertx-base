package com.yjl.vertx.base.com.anno.initializer;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface FirstClassComponent {
	ComponentInitializer[] value();
}
