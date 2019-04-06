package com.yjl.vertx.base.webclient.anno.request;

import com.yjl.vertx.base.webclient.enumeration.ClientInstanceInitLevel;
import io.vertx.core.http.HttpMethod;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Request {
	String path();
	ClientInstanceInitLevel initLevel() default ClientInstanceInitLevel.INHERIT;
	HttpMethod method() default HttpMethod.GET;
}
