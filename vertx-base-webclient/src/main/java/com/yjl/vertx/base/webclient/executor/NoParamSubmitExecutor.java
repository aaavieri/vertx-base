package com.yjl.vertx.base.webclient.executor;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;

import java.lang.reflect.Method;
import java.util.Map;

public class NoParamSubmitExecutor extends AbstractRequestExecutor {
	@Override
	public boolean isMatch(Method method) {
		return this.getRequestData(method).noneMatch(this.isFormSubmit.or(this.isJsonSubmit));
	}

	@Override
	protected Future<HttpResponse<Buffer>> sendRequest(HttpRequest<Buffer> request, Method method, Map<String, Object> paramMap) {
		Future<HttpResponse<Buffer>> responseFuture = Future.future();
		request.send(responseFuture);
		return responseFuture;
	}
}
