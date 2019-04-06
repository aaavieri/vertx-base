package com.yjl.vertx.base.webclient.adaptor;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;

public class ToJsonArrayAdaptor extends AbstractResponseAdaptor<JsonArray> {
	@Override
	public JsonArray adapt(HttpResponse<Buffer> bufferHttpResponse) {
		return bufferHttpResponse.bodyAsJsonArray();
	}
}
