package com.yjl.vertx.base.web.factory.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.Order;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.com.util.StringUtil;
import com.yjl.vertx.base.web.anno.component.RestRouteMapping;
import com.yjl.vertx.base.web.anno.component.RestRouteV1Handler;
import com.yjl.vertx.base.web.anno.handler.RestV1HandlerInject;
import com.yjl.vertx.base.web.handler.HandlerWrapper;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ComponentInitializer(factoryClass = RestHandlerV1Factory.class)
public class RestRouteV1Factory extends BaseRestRouteFactory {

	@Inject
	@RestV1HandlerInject
	private Set<Object> handlers;

	@Override
	protected List<HandlerWrapper> getHandlerWrapperList() {
		return this.handlers.stream().flatMap(handler ->
			ReflectionsUtil.getPublicMethods(handler.getClass(), RestRouteMapping.class, Handler.class).stream()
				.filter(this::checkGenericInfo)
				.map(method -> {
					RestRouteV1Handler routeHandler = method.getDeclaringClass().getAnnotation(RestRouteV1Handler.class);
					try {
						Handler<RoutingContext> methodHandler = ReflectionsUtil.autoCast(method.invoke(handler));
						RestRouteMapping routeMapping = method.getAnnotation(RestRouteMapping.class);
						Order order = method.getAnnotation(Order.class);
						return new HandlerWrapper().autoHandleError(routeMapping.autoHandleError()).regexp(routeMapping.regexp())
							.method(routeMapping.method()).descript(routeMapping.descript()).url(StringUtil.concatPath(routeHandler.value(), routeMapping.value()))
							.handler(methodHandler).order(order == null ? Integer.MAX_VALUE : order.value());
					} catch (Throwable e) {
						this.getLogger().error(e.getMessage(), e);
						throw new FrameworkException(e);
					}
				})
		).collect(Collectors.toList());
	}
}
