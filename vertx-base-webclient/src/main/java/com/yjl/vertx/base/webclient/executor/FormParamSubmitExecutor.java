package com.yjl.vertx.base.webclient.executor;

import com.yjl.vertx.base.com.util.ReflectionsUtil;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

public class FormParamSubmitExecutor extends AbstractRequestExecutor {
	@Override
	public boolean isMatch(Method method) {
		return this.getRequestData(method).anyMatch(this.isFormSubmit);
	}

	@Override
	protected Future<HttpResponse<Buffer>> sendRequest(HttpRequest<Buffer> request, Method method, Map<String, Object> paramMap) {
		MultiMap formAttrMap = this.getRequestData(method).filter(this.isFormSubmit)
			.reduce(MultiMap.caseInsensitiveMultiMap(), (formMap, requestData) -> {
				Object object = this.getRequestDataValue(requestData, paramMap);
				if (object instanceof Collection) {
					ReflectionsUtil.<Collection<Object>>autoCast(object).forEach(record -> formMap.add(requestData.key(), String.valueOf(record)));
				} else if (object != null && object.getClass().isArray()) {
					Stream.of(object).forEach(record -> formMap.add(requestData.key(), String.valueOf(record)));
				} else {
					formMap.add(requestData.key(), String.valueOf(object));
				}
				return formMap;
			}, MultiMap::addAll);
		Future<HttpResponse<Buffer>> responseFuture = Future.future();
		request.sendForm(formAttrMap, responseFuture);
		return responseFuture;
	}
}
