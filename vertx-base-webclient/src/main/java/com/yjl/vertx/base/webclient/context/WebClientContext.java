package com.yjl.vertx.base.webclient.context;

import com.yjl.vertx.base.com.util.StringUtil;
import com.yjl.vertx.base.webclient.adaptor.AbstractResponseAdaptor;
import com.yjl.vertx.base.webclient.anno.component.RequestClient;
import com.yjl.vertx.base.webclient.anno.request.Request;
import com.yjl.vertx.base.webclient.enumeration.ClientInstanceInitLevel;
import com.yjl.vertx.base.webclient.executor.AbstractRequestExecutor;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.util.Map;

@Data
@Accessors(fluent = true)
public class WebClientContext {

	private WebClient webClient;

	private Method method;

	private AbstractResponseAdaptor<Buffer> responseAdaptor;

	private AbstractRequestExecutor requestExecutor;

	private RequestClient requestClient;

	private Request request;

	private ClientInstanceInitLevel initLevel;

	public WebClientContext copy() {
		return new WebClientContext().method(this.method).requestExecutor(this.requestExecutor).responseAdaptor(this.responseAdaptor)
			.requestClient(this.requestClient).request(this.request).initLevel(this.initLevel);
	}

	public HttpRequest<Buffer> initRequest(Map<String, Object> paramMap) {
		String path = StringUtil.replaceParam(this.request.path(), paramMap);
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		return this.webClient.request(this.request.method(),
			this.requestClient.port(), this.requestClient.host(), path)
			.ssl(this.requestClient.ssl());
	}
}
