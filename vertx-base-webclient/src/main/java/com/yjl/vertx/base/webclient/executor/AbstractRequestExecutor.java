package com.yjl.vertx.base.webclient.executor;

import com.yjl.vertx.base.com.util.StringUtil;
import com.yjl.vertx.base.webclient.anno.request.RequestData;
import com.yjl.vertx.base.webclient.enumeration.RequestDataType;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractRequestExecutor {


	protected Predicate<RequestData> isFormSubmit = requestData -> requestData.type().equals(RequestDataType.FORM_PARAM);

	protected Predicate<RequestData> isJsonSubmit = requestData -> requestData.type().equals(RequestDataType.JSON_PARAM);

	public abstract boolean isMatch(Method method);

	public Future<HttpResponse<Buffer>> execute(HttpRequest<Buffer> httpRequest, Method method, Map<String, Object> paramMap) {
//		Request request = method.getAnnotation(Request.class);
//		RequestClient requestClient = method.getDeclaringClass().getAnnotation(RequestClient.class);
//		String path = StringUtil.replaceParam(request.path(), paramMap);
//		HttpRequest<Buffer> httpRequest = webClient.request(request.method(), requestClient.port(), requestClient.host(), path).ssl(requestClient.ssl());
		this.getRequestData(method).forEach(requestData -> {
			if (requestData.type().equals(RequestDataType.HEADER)) {
				httpRequest.putHeader(requestData.key(), String.valueOf(this.getRequestDataValue(requestData, paramMap)));
			} else if (requestData.type().equals(RequestDataType.QUERY_PARAM)) {
				httpRequest.addQueryParam(requestData.key(), String.valueOf(this.getRequestDataValue(requestData, paramMap)));
			}
		});
		return this.sendRequest(httpRequest, method, paramMap);
	}

	protected Object getRequestDataValue(RequestData requestData, Map<String, Object> paramMap) {
		if (!StringUtil.isBlank(requestData.value())) {
			return requestData.value();
		} else if (!StringUtil.isBlank(requestData.paramKey())) {
			return paramMap.get(requestData.paramKey());
		} else {
			return paramMap.get(requestData.key());
		}
	}

	protected Stream<RequestData> getRequestData(Method method) {
		return Stream.of(method.getAnnotationsByType(RequestData.class));
	}

	protected abstract Future<HttpResponse<Buffer>> sendRequest(HttpRequest<Buffer> request, Method method, Map<String, Object> paramMap);
}
