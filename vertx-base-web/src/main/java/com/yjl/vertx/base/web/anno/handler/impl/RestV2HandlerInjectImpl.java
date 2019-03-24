package com.yjl.vertx.base.web.anno.handler.impl;

import com.yjl.vertx.base.web.anno.handler.RestV2HandlerInject;

import java.lang.annotation.Annotation;

public class RestV2HandlerInjectImpl implements RestV2HandlerInject {

	private String value;

	public RestV2HandlerInjectImpl(String value) {
		this.value = value;
	}

	@Override
	public String value() {
		return this.value;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return RestV2HandlerInject.class;
	}

	public int hashCode() {
		return 127 * "value".hashCode() ^ this.value.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof RestV2HandlerInject)) {
			return false;
		} else {
			RestV2HandlerInject other = (RestV2HandlerInject)o;
			return this.value.equals(other.value());
		}
	}
}
