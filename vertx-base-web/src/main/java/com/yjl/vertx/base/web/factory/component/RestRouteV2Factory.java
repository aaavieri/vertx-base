package com.yjl.vertx.base.web.factory.component;

import com.yjl.vertx.base.com.anno.Order;
import com.yjl.vertx.base.com.util.OrderUtil;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.com.verticle.ApplicationContext;
import com.yjl.vertx.base.web.anno.component.RestRouteV2Handler;
import com.yjl.vertx.base.web.handler.BaseRouteV2Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestRouteV2Factory extends BaseRestRouteFactory {

	private final static Logger logger = LoggerFactory.getLogger(RestRouteV2Factory.class);

	private List<Class<? extends BaseRouteV2Handler>> handlerList;

	@Override
	public void configure() {
		logger.info("start configure");
		this.handlerList = Stream.of(this.metaData.value()).flatMap(packageName -> ReflectionsUtil.getClasses(packageName, BaseRouteV2Handler.class, RestRouteV2Handler.class).stream())
				.peek(clazz -> this.bind(clazz).asEagerSingleton()).collect(Collectors.toList());
	}

	@Override
	protected void installRoute() {
		this.handlerList.stream().sorted(Comparator.comparingInt(handleClass ->
			OrderUtil.sortOrderDescFunc().applyAsInt(handleClass.getAnnotation(Order.class))
		))
		.forEachOrdered(handlerClass -> {
			BaseRouteV2Handler handler = ApplicationContext.getInstance().getContext().getProvider(handlerClass).get();
			RestRouteV2Handler restRouteV2Handler = handlerClass.getAnnotation(RestRouteV2Handler.class);
			Stream.of(restRouteV2Handler.value()).forEach(restRouteMapping ->
				this.bindOneRoute(restRouteMapping.value(), restRouteMapping, handler::handle, handler::handleFailure)
			);
		});
	}
}
