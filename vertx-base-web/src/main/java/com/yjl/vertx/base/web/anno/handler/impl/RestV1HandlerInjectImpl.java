package com.yjl.vertx.base.web.anno.handler.impl;

import com.yjl.vertx.base.web.anno.handler.RestV1HandlerInject;

import java.lang.annotation.Annotation;

public class RestV1HandlerInjectImpl implements RestV1HandlerInject {

	private String value;

	public RestV1HandlerInjectImpl(String value) {
		this.value = value;
	}

	@Override
	public String value() {
		return this.value;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return RestV1HandlerInject.class;
	}

	public int hashCode() {
		return 127 * "value".hashCode() ^ this.value.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof RestV1HandlerInject)) {
			return false;
		} else {
			RestV1HandlerInject other = (RestV1HandlerInject)o;
			return this.value.equals(other.value());
		}
	}
}
