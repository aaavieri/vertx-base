package com.yjl.vertx.base.webclient.factory;

import com.google.inject.Inject;
import com.yjl.vertx.base.autoroute.anno.AutoRouteIf;
import com.yjl.vertx.base.autoroute.util.AutoRouteUtil;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.builder.ParamMapBuilder;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.web.factory.component.BaseRestRouteFactory;
import com.yjl.vertx.base.web.factory.component.DefaultFailureHandlerFactory;
import com.yjl.vertx.base.web.factory.component.HttpServerFactory;
import com.yjl.vertx.base.web.handler.HandlerWrapper;
import com.yjl.vertx.base.webclient.anno.component.RequestClient;
import com.yjl.vertx.base.webclient.context.WebClientContext;
import com.yjl.vertx.base.webclient.context.WebClientContextCache;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;

import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ComponentInitializer(factoryClass = ResponseAdaptorFactory.class, value = "com.yjl.vertx.base.webclient.adaptor")
@ComponentInitializer(factoryClass = RequestExecutorFactory.class, value = "com.yjl.vertx.base.webclient.executor")
@ComponentInitializer(factoryClass = DefaultFailureHandlerFactory.class)
@ComponentInitializer(factoryClass = HttpServerFactory.class)
@ComponentInitializer(factoryClass = WebClientContextCacheFactory.class)
public class AutoRouteWebClientFactory extends BaseRestRouteFactory {

	@Inject
	private WebClientContextCache webClientContextCache;

	@Override
	protected List<HandlerWrapper> getHandlerWrapperList() {
		return Stream.concat(Stream.of(this.metaData.value()).flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName, RequestClient.class).stream()),
			Stream.of(this.metaData.value()).flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName, AutoRouteIf.class).stream()))
			.filter(clazz -> {
				if (!clazz.isInterface()) {
					this.getLogger().warn("warning: {} is not interface, skipped", clazz.getName());
					return false;
				}
				return true;
			})
			.flatMap(clazz -> Stream.of(clazz.getMethods())
				.map(method -> {
					Handler<RoutingContext> handler = context -> {
						JsonObject jsonObject = AutoRouteUtil.getRequestParam(context);
						WebClientContext webClientContext = this.webClientContextCache.getContext(method);
						Map<String, Object> paramMap = new ParamMapBuilder().buildJsonObject(jsonObject).getParamMap();
						HttpRequest<Buffer> httpRequest = webClientContext.initRequest(paramMap);
						webClientContext.requestExecutor().execute(httpRequest, method, paramMap)
							.setHandler(asyncResult -> {
								if (asyncResult.succeeded()) {
									HttpResponse<Buffer> bufferHttpResponse = asyncResult.result();
									if (bufferHttpResponse.statusCode() >= 200 && bufferHttpResponse.statusCode() < 300) {
										Object adaptResult = webClientContext.responseAdaptor().adapt(bufferHttpResponse);
										context.response().end(Json.encodeToBuffer(adaptResult));
									} else {
										context.fail(new FrameworkException().message(bufferHttpResponse.statusMessage()));
									}
								} else {
									context.fail(asyncResult.cause());
								}
							});
					};
					return AutoRouteUtil.getHandlerWrapper(method, handler);
				})
			).collect(Collectors.toList());
	}
}
