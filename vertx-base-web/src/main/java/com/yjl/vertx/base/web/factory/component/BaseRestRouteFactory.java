package com.yjl.vertx.base.web.factory.component;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.web.handler.HandlerWrapper;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;

public abstract class BaseRestRouteFactory extends BaseAnnotationComponentFactory {

	@Inject
	@Named("defaultFailureHandler")
	private Handler<RoutingContext> defaultFailureHandler;

	@Inject
	protected Router router;

	@Inject
	protected HttpServer server;

	public void configure() {
		this.getHandlerWrapperList().stream().sorted(Comparator.comparingInt(HandlerWrapper::order)).forEach(this::bindOneRoute);
	}

	protected boolean checkGenericInfo(Method method) {
		Type genericReturnType = method.getGenericReturnType();
		boolean checkResult = false;
		if (genericReturnType instanceof ParameterizedType) {
			Type[] typeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
			checkResult = typeArguments.length == 1 && RoutingContext.class.getName().equals(typeArguments[0].getTypeName());
		}
		if (!checkResult) {
			System.out.println(String.format("Skipped %s, because the generic info is not match Handler<RoutingContext>", method.toGenericString()));
		}
		return checkResult;
	}

	protected abstract List<HandlerWrapper> getHandlerWrapperList();

	protected void bindOneRoute(final HandlerWrapper handlerWrapper) {
		String methodName = handlerWrapper.method().name().toLowerCase() + (handlerWrapper.regexp() ? "WithRegex" : "");
		try {
			MethodHandle methodHandle = MethodHandles.publicLookup().findVirtual(this.router.getClass(), methodName, MethodType.methodType(Route.class, String.class));
			Route route = ReflectionsUtil.autoCast(methodHandle.invoke(this.router, handlerWrapper.url()));
			route.handler(handlerWrapper.handler());
			if (handlerWrapper.autoHandleError()) {
				route.failureHandler(this.defaultFailureHandler);
			}
		} catch (Throwable throwable) {
			this.getLogger().error(throwable.getMessage(), throwable);
			throw new FrameworkException(throwable);
		}
	}
}
