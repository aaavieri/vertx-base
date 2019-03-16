package com.yjl.vertx.base.web.handler;

import com.yjl.vertx.base.web.factory.handler.FailureHandlerFactory;
import io.vertx.ext.web.RoutingContext;

public abstract class BaseRouteV2Handler {
	public abstract void handle(RoutingContext context);

	public void handleFailure(RoutingContext context) {
		FailureHandlerFactory.getDefault().handle(context);
	}
}
