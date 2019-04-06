package com.yjl.vertx.base.webclient.anno.component;

import com.yjl.vertx.base.webclient.enumeration.ClientInstanceInitLevel;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestClient {
	String host();
	boolean ssl() default false;
	int port() default 80;
	ClientInstanceInitLevel initLevel() default ClientInstanceInitLevel.SHARE_ALL;
}
