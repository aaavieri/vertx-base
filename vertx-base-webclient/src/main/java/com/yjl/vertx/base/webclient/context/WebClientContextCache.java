package com.yjl.vertx.base.webclient.context;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.webclient.adaptor.AbstractResponseAdaptor;
import com.yjl.vertx.base.webclient.anno.component.RequestClient;
import com.yjl.vertx.base.webclient.anno.request.Request;
import com.yjl.vertx.base.webclient.anno.request.WebOptions;
import com.yjl.vertx.base.webclient.enumeration.ClientInstanceInitLevel;
import com.yjl.vertx.base.webclient.executor.AbstractRequestExecutor;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

public class WebClientContextCache {

	@Inject
	private Vertx vertx;

	@Inject(optional = true)
	@Config("app.webclient.option")
	protected JsonObject webClientOptions = new JsonObject();

	@Inject
	private Set<AbstractRequestExecutor> executorSet;

	@Inject
	private Set<AbstractResponseAdaptor> adaptorSet;

	private WebClient globalWebClient = null;

	private Map<Class, WebClient> shareInClientMap = new HashMap<>();

	private List<WebClientContext> contextList = new ArrayList<>();

	public WebClientContext getContext(Method method) {
		WebClientContext webClientContext = this.contextList.stream().filter(context -> context.method().equals(method))
			.findFirst().orElseGet(() -> {
				WebClientContext newWebClientContext = this.initWebClientContext(method);
				AbstractRequestExecutor requestExecutor = this.executorSet.stream().filter(executor -> executor.isMatch(method)).findFirst()
					.orElseThrow(() -> new FrameworkException().message("can not find executor for:" + method.getName()));
				AbstractResponseAdaptor<Buffer> responseAdaptor = ReflectionsUtil.autoCast(this.adaptorSet.stream().filter(adaptor -> adaptor.isMatch(method)).findFirst()
					.orElseThrow(() -> new FrameworkException().message("can not find adaptor for:" + method.getName())));
				newWebClientContext.requestExecutor(requestExecutor).responseAdaptor(responseAdaptor);
				if (!newWebClientContext.initLevel().equals(ClientInstanceInitLevel.SHARE_ACCESS)) {
					newWebClientContext.webClient(this.getWebClient(method, newWebClientContext.initLevel()));
				}
				this.contextList.add(newWebClientContext);
				return newWebClientContext;
			});
		if (webClientContext.initLevel().equals(ClientInstanceInitLevel.SHARE_ACCESS)) {
			return webClientContext.copy().webClient(this.getMethodLevelWebClient(method));
		} else {
			return webClientContext;
		}
	}

	private WebClient getMethodLevelWebClient(Method method) {
		Class<?> clientIf = method.getDeclaringClass();
		JsonObject options = new JsonObject().mergeIn(this.webClientOptions, true);
		Stream.concat(Stream.of(clientIf.getAnnotationsByType(WebOptions.class)),
			Stream.of(method.getAnnotationsByType(WebOptions.class)))
			.forEach(defineOption -> options.put(defineOption.key(), defineOption.value()));
		return WebClient.create(this.vertx, options.mapTo(WebClientOptions.class));
	}

	private WebClient getClientLevelWebClient(Method method) {
		Class<?> clientIf = method.getDeclaringClass();
		JsonObject options = new JsonObject().mergeIn(this.webClientOptions, true);
		if (!this.shareInClientMap.containsKey(clientIf)) {
			Stream.of(clientIf.getAnnotationsByType(WebOptions.class)).forEach(defineOption -> options.put(defineOption.key(), defineOption.value()));
			this.shareInClientMap.put(clientIf, WebClient.create(this.vertx, options.mapTo(WebClientOptions.class)));
		}
		return this.shareInClientMap.get(clientIf);
	}

	private WebClient getGlobalWebClient() {
		if (this.globalWebClient == null) {
			this.globalWebClient = WebClient.create(this.vertx, this.webClientOptions.mapTo(WebClientOptions.class));
		}
		return this.globalWebClient;
	}

	private WebClientContext initWebClientContext(Method method) {
		Class<?> clientIf = method.getDeclaringClass();
		RequestClient requestClient = clientIf.getAnnotation(RequestClient.class);
		Request request = method.getAnnotation(Request.class);
		if (request == null) {
			throw new FrameworkException().message("method is not annotated with @Request: " + clientIf.getName() + "#" + method.getName());
		} else if (!ReflectionsUtil.isFutureParamType(method.getGenericReturnType())) {
			throw new FrameworkException().message("method is not return io.vertx.core.Future<T>: " + clientIf.getName() + "#" + method.getName());
		}
		return new WebClientContext().method(method).request(request).requestClient(requestClient).initLevel(request.initLevel().equals(ClientInstanceInitLevel.INHERIT)
			? requestClient.initLevel() : request.initLevel());
	}

	private WebClient getWebClient(Method method, ClientInstanceInitLevel initLevel) {
		if (initLevel.equals(ClientInstanceInitLevel.SHARE_METHOD)) {
			return this.getMethodLevelWebClient(method);
		} else if (initLevel.equals(ClientInstanceInitLevel.SHARE_CLIENT)) {
			return this.getClientLevelWebClient(method);
		} else {
			return this.getGlobalWebClient();
		}
	}
}
