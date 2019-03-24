package com.yjl.vertx.base.web.factory.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.web.anno.component.RestRouteMapping;
import com.yjl.vertx.base.web.enumeration.RouteMethod;
import com.yjl.vertx.base.web.factory.handler.FailureHandlerFactory;
import com.yjl.vertx.base.web.handler.HandlerWrapper;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;

public abstract class BaseRestRouteFactory extends BaseAnnotationComponentFactory {

	protected Router router;

	@Inject
	protected Vertx vertx;

	@Config
	@Inject
	private JsonObject config;

	@Inject
	@Config("app.port")
	private int port = this.defaultPort();

	public void configure() {
		HttpServer server = vertx.createHttpServer();
		this.router = Router.router(this.vertx);
		this.router.route().handler(CookieHandler.create())
			.handler(BodyHandler.create())
			.produces("application/json;charset=UTF-8")
			.handler(ResponseContentTypeHandler.create());
		this.getHandlerWrapperList().stream().sorted(Comparator.comparingInt(HandlerWrapper::order)).forEach(this::bindOneRoute);
		server.requestHandler(router).listen(this.getPort());
	}

	protected int getPort() {
		return this.port;
	}

	protected int defaultPort() {
		return 8080;
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
			if (handlerWrapper.failHandler() != null) {
				route.failureHandler(handlerWrapper.failHandler());
			} else if (handlerWrapper.autoHandleError()) {
				route.failureHandler(FailureHandlerFactory.getDefault());
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
}
