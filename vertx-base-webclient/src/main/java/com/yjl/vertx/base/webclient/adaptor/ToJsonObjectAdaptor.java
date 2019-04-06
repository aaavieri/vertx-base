package com.yjl.vertx.base.webclient.adaptor;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;

public class ToJsonObjectAdaptor extends AbstractResponseAdaptor<JsonObject> {
	@Override
	public JsonObject adapt(HttpResponse<Buffer> bufferHttpResponse) {
		return bufferHttpResponse.bodyAsJsonObject();
	}
}
