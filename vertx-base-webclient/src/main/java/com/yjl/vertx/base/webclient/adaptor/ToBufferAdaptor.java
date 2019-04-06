package com.yjl.vertx.base.webclient.adaptor;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;

public class ToBufferAdaptor extends AbstractResponseAdaptor<Buffer> {
	@Override
	public Buffer adapt(HttpResponse<Buffer> bufferHttpResponse) {
		return bufferHttpResponse.body();
	}
}
