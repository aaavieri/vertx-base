package com.yjl.vertx.base.webclient.factory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.generator.ProxyGeneratorIf;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.webclient.anno.component.RequestClient;

import java.util.stream.Stream;

@ComponentInitializer(factoryClass = RequestExecutorFactory.class, value = "com.yjl.vertx.base.webclient.executor")
@ComponentInitializer(factoryClass = ResponseAdaptorFactory.class, value = "com.yjl.vertx.base.webclient.adaptor")
@ComponentInitializer(factoryClass = WebClientContextCacheFactory.class)
@ComponentInitializer(factoryClass = DefaultWebClientGeneratorFactory.class)
public class WebClientFactory extends BaseAnnotationComponentFactory {

	@Inject
    @Named("webClientGenerator")
	private ProxyGeneratorIf webClientGenerator;

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
			.forEach(clazz -> this.bind(clazz).toInstance(ReflectionsUtil.autoCast(this.webClientGenerator.getProxyInstance(clazz))));
	}

//	private <T> T getProxyInstance(Class<T> clientIf) {
//		InvocationHandler invocationHandler = (proxy, method, args) -> {
//			WebClientContext webClientContext = this.webClientContextCache.getContext(method);
//
//			Map<String, Object> paramMap = new ParamMapBuilder().buildMethodCall(method, args).getParamMap();
//			HttpRequest<Buffer> httpRequest = webClientContext.initRequest(paramMap);
//			return webClientContext.requestExecutor().execute(httpRequest, method, paramMap)
//				.compose(bufferHttpResponse -> {
//					Future<Object> future = Future.future();
//					if (bufferHttpResponse.statusCode() >= 200 && bufferHttpResponse.statusCode() < 300) {
//						Object returnValue = webClientContext.responseAdaptor().adapt(bufferHttpResponse);
//						future.complete(returnValue);
//					} else {
//						future.fail(new FrameworkException().message(bufferHttpResponse.statusMessage()));
//					}
//					return future;
//				});
//		};
//		return ReflectionsUtil.autoCast(Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { clientIf },
//			invocationHandler));
//	}
}
