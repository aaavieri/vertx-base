package com.yjl.vertx.base.web.factory.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.Order;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.web.anno.component.RestRouteV2Handler;
import com.yjl.vertx.base.web.anno.handler.RestV2HandlerInject;
import com.yjl.vertx.base.web.handler.BaseRouteV2Handler;
import com.yjl.vertx.base.web.handler.HandlerWrapper;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ComponentInitializer(factoryClass = RestHandlerV2Factory.class)
@ComponentInitializer(factoryClass = HttpServerFactory.class)
public class RestRouteV2Factory extends BaseRestRouteFactory {

	@Inject
	@RestV2HandlerInject
	private Set<BaseRouteV2Handler> handlers;

	@Override
	protected List<HandlerWrapper> getHandlerWrapperList() {
		return handlers.stream().flatMap(handler -> {
			RestRouteV2Handler restRouteV2Handler = handler.getClass().getAnnotation(RestRouteV2Handler.class);
			Order order = handler.getClass().getAnnotation(Order.class);
			Handler<RoutingContext> methodHandler = handler::handle;

			return Stream.of(restRouteV2Handler.value()).map(restRouteMapping ->
				new HandlerWrapper().autoHandleError(restRouteMapping.autoHandleError()).regexp(restRouteMapping.regexp())
					.method(restRouteMapping.method()).descript(restRouteMapping.descript()).url(restRouteMapping.value())
					.handler(methodHandler).order(order == null ? Integer.MAX_VALUE : order.value())
                    .handlerClass(handler.getClass()).handlerMethod("handleSuccess")
			);
		}).collect(Collectors.toList());
	}
}
