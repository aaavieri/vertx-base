package com.yjl.vertx.base.webclient.anno.request;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MultiWebOptions {
	WebOptions[] value();
}
