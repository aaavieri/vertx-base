package com.yjl.vertx.base.webclient.adaptor;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;

public class ToStringAdaptor extends AbstractResponseAdaptor<String> {
	@Override
	public String adapt(HttpResponse<Buffer> bufferHttpResponse) {
		return bufferHttpResponse.bodyAsString();
	}
}
