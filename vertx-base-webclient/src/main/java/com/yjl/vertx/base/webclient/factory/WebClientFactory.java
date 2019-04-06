package com.yjl.vertx.base.webclient.factory;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.builder.ParamMapBuilder;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.com.util.StringUtil;
import com.yjl.vertx.base.webclient.anno.component.RequestClient;
import com.yjl.vertx.base.webclient.context.WebClientContext;
import com.yjl.vertx.base.webclient.context.WebClientContextCache;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.stream.Stream;

@ComponentInitializer(factoryClass = RequestExecutorFactory.class, value = "com.yjl.vertx.base.webclient.executor")
@ComponentInitializer(factoryClass = ResponseAdaptorFactory.class, value = "com.yjl.vertx.base.webclient.adaptor")
@ComponentInitializer(factoryClass = WebClientContextCacheFactory.class)
public class WebClientFactory extends BaseAnnotationComponentFactory {

	@Inject
	private WebClientContextCache webClientContextCache;

	@Override
	public void configure() {
		Stream.of(this.metaData.value()).flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName, RequestClient.class).stream())
			.filter(clazz -> {
				if (!clazz.isInterface()) {
					this.getLogger().warn("warning: {} is not interface, skipped", clazz.getName());
					return false;
				}
				return true;
			})
			.forEach(clazz -> this.bind(clazz).toInstance(ReflectionsUtil.autoCast(this.getProxyInstance(clazz))));
	}

	private <T> T getProxyInstance(Class<T> clientIf) {
		InvocationHandler invocationHandler = (proxy, method, args) -> {
			WebClientContext webClientContext = this.webClientContextCache.getContext(method);

			Map<String, Object> paramMap = new ParamMapBuilder().buildMethodCall(method, args).getParamMap();
			HttpRequest<Buffer> httpRequest = webClientContext.initRequest(paramMap);
			return webClientContext.requestExecutor().execute(httpRequest, method, paramMap)
				.compose(bufferHttpResponse -> {
					Future<Object> future = Future.future();
					if (bufferHttpResponse.statusCode() >= 200 && bufferHttpResponse.statusCode() < 300) {
						Object returnValue = webClientContext.responseAdaptor().adapt(bufferHttpResponse);
						future.complete(returnValue);
					} else {
						future.fail(new FrameworkException().message(bufferHttpResponse.statusMessage()));
					}
					return future;
				});
		};
		return ReflectionsUtil.autoCast(Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { clientIf },
			invocationHandler));
	}
}
