package com.yjl.vertx.base.web.factory.component;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.web.handler.HandlerWrapper;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public abstract class BaseRestRouteFactory extends BaseAnnotationComponentFactory {

	@Inject
	@Named("defaultFailureHandler")
	private Handler<RoutingContext> defaultFailureHandler;

	@Inject(optional = true)
    @Config("app.route.minOrder")
    protected int minRouteOrder = 10;

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
			this.getLogger().warn("Skipped {}, because the generic info is not match Handler<RoutingContext>", method.toGenericString());
		}
		return checkResult;
	}

	protected abstract List<HandlerWrapper> getHandlerWrapperList();

	protected void bindOneRoute(final HandlerWrapper handlerWrapper) {
//		String methodName = (handlerWrapper.method() == null ? "route" : handlerWrapper.method().name().toLowerCase())
//            + (handlerWrapper.regexp() ? "WithRegex" : "");
		try {
//			MethodHandle methodHandle = MethodHandles.publicLookup().findVirtual(this.router.getClass(), methodName, MethodType.methodType(Route.class, String.class));
//			Route route = ReflectionsUtil.<Route>autoCast(methodHandle.invoke(this.router.rou, handlerWrapper.url())).order(handlerWrapper.order());
            Function<String, Route> noMethodFunction = handlerWrapper.regexp() ? this.router::routeWithRegex : this.router::route;
            BiFunction<HttpMethod, String, Route> methodFunction = handlerWrapper.regexp() ? this.router::routeWithRegex : this.router::route;
            Route route = handlerWrapper.method() == null
                ? noMethodFunction.apply(handlerWrapper.url()) : methodFunction.apply(handlerWrapper.method(), handlerWrapper.url());
            this.getLogger().info("bind {} to {}#{}", handlerWrapper.url(), handlerWrapper.handlerClass().getName(), handlerWrapper.handlerMethod());
			route.order(this.calcOrder(handlerWrapper.order())).handler(handlerWrapper.handler());
//            route.handler(handlerWrapper.handler());
			if (handlerWrapper.autoHandleError()) {
				route.failureHandler(this.defaultFailureHandler);
			}
		} catch (Throwable throwable) {
			this.getLogger().error(throwable.getMessage(), throwable);
			throw new FrameworkException(throwable);
		}
	}

	protected int calcOrder(int order) {
	    return Integer.max(Integer.max(order + this.minRouteOrder, this.minRouteOrder), order);
    }
}
