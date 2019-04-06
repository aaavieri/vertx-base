package com.yjl.vertx.base.webclient.anno.request;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(MultiFormAttrParam.class)
public @interface FormAttrParam {
	String key();
	String paramKey();
}
