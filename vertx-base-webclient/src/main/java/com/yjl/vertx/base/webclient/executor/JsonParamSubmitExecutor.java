package com.yjl.vertx.base.webclient.executor;

import com.yjl.vertx.base.com.util.JsonUtil;
import com.yjl.vertx.base.webclient.anno.request.RequestData;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

public class JsonParamSubmitExecutor extends AbstractRequestExecutor {
	@Override
	public boolean isMatch(Method method) {
		return this.getRequestData(method).anyMatch(this.isJsonSubmit);
	}

	@Override
	protected Future<HttpResponse<Buffer>> sendRequest(HttpRequest<Buffer> request, Method method, Map<String, Object> paramMap) {
		Object jsonData = this.getRequestData(method).filter(this.isJsonSubmit)
			.filter(RequestData::whole).findFirst().map(requestData -> this.getRequestDataValue(requestData, paramMap))
			.orElseGet(() -> this.getRequestData(method).filter(this.isJsonSubmit)
				.reduce(new JsonObject(),
					(jsonObject, requestData) -> jsonObject.put(requestData.key(),
						JsonUtil.getJsonAddableObject(this.getRequestDataValue(requestData, paramMap))),
					JsonObject::mergeIn)
			);
		Future<HttpResponse<Buffer>> responseFuture = Future.future();
		request.sendJson(jsonData, responseFuture);
		return responseFuture;
	}
}
