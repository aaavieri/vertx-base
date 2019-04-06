package com.yjl.vertx.base.webclient.adaptor;

import com.yjl.vertx.base.com.function.AdaptorFunction;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public abstract class AbstractResponseAdaptor<R> implements AdaptorFunction<HttpResponse<Buffer>, R> {

	public boolean isMatch(Method method) {
		return ReflectionsUtil.compareType(ReflectionsUtil.<ParameterizedType>autoCast(this.getClass().getGenericSuperclass())
			.getActualTypeArguments()[0], ReflectionsUtil.getFutureActuleParamType(method.getGenericReturnType()), false);
	}
}
