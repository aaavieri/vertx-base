package com.yjl.vertx.base.web.factory.component;

import com.yjl.vertx.base.com.anno.Order;
import com.yjl.vertx.base.com.util.OrderUtil;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.com.util.StringUtil;
import com.yjl.vertx.base.com.verticle.ApplicationContext;
import com.yjl.vertx.base.web.anno.component.RestRouteV1Handler;
import com.yjl.vertx.base.web.anno.component.RestRouteMapping;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestRouteV1Factory extends BaseRestRouteFactory {

	private List<Class<?>> handlerList;

	@Override
	public void configure() {
		this.handlerList = Stream.of(this.metaData.value()).flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName, RestRouteV1Handler.class).stream())
			.peek(clazz -> this.bind(clazz).asEagerSingleton()).collect(Collectors.toList());
	}

	protected void installRoute() {
		this.handlerList.stream().flatMap(clazz -> ReflectionsUtil.getPublicMethods(clazz, RestRouteMapping.class, Handler.class).stream())
			.filter(this::checkGenericInfo)
			.sorted(Comparator.comparingInt(method ->
				OrderUtil.getSortOrderDesc(method.getAnnotation(Order.class))
			))
			.forEachOrdered(method -> {
				Object instance = ApplicationContext.getInstance().getContext().getProvider(method.getDeclaringClass()).get();
				RestRouteV1Handler routeHandler = method.getDeclaringClass().getAnnotation(RestRouteV1Handler.class);
				try {
					Handler<RoutingContext> methodHandler = ReflectionsUtil.autoCast(method.invoke(instance));
					RestRouteMapping routeMapping = method.getAnnotation(RestRouteMapping.class);
					this.bindRoute(routeHandler, routeMapping, methodHandler);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			});
	}

	private void bindRoute(final RestRouteV1Handler routeHandler, final RestRouteMapping routeMapping, final Handler<RoutingContext> methodHandler) {
		String url = StringUtil.concatPath(routeHandler.value(), routeMapping.value());
		this.bindOneRoute(url, routeMapping, methodHandler);
	}
}