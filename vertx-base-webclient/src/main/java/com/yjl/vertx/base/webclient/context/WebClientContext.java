package com.yjl.vertx.base.webclient.context;

import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.com.util.StringUtil;
import com.yjl.vertx.base.webclient.adaptor.AbstractResponseAdaptor;
import com.yjl.vertx.base.webclient.anno.component.RequestClient;
import com.yjl.vertx.base.webclient.anno.request.Request;
import com.yjl.vertx.base.webclient.enumeration.ClientInstanceInitLevel;
import com.yjl.vertx.base.webclient.executor.AbstractRequestExecutor;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
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

	private JsonObject extendConfig;

	private Map<String, String> env;

	private Method method;

	private AbstractResponseAdaptor<Buffer> responseAdaptor;

	private AbstractRequestExecutor requestExecutor;

	private RequestClient requestClient;

	private Request request;

	private ClientInstanceInitLevel initLevel;

	public WebClientContext copy() {
		return new WebClientContext().method(this.method).extendConfig(this.extendConfig).env(this.env)
            .requestExecutor(this.requestExecutor).responseAdaptor(this.responseAdaptor)
			.requestClient(this.requestClient).request(this.request).initLevel(this.initLevel);
	}

	public HttpRequest<Buffer> initRequest(Map<String, Object> paramMap) {
        String path = this.replaceConfigAndEnv(this.request.path());
        path = StringUtil.replaceParam(path, paramMap);
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        String host = this.replaceConfigAndEnv(this.requestClient.host());
		return this.webClient.request(this.request.method(),
			this.requestClient.port(), host, path)
			.ssl(this.requestClient.ssl());
	}

	private String replaceConfigAndEnv(String originalPath) {
        String path = StringUtil.replaceParam(originalPath, this.extendConfig);
        return StringUtil.replaceParam(path, ReflectionsUtil.<Map<String, Object>>autoCast(env));
    }
}
