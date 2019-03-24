package com.yjl.vertx.base.web.handler;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public abstract class BaseRouteV2Handler {

	@Inject
	@Named("defaultFailureHandler")
	private Handler<RoutingContext> defaultFailureHandler;

	public void handle(RoutingContext context) {
		this.handleSuccess(context).setHandler(voidAsyncResult -> {
			if (voidAsyncResult.failed()) {
				this.handleFailure(context);
			}
		});
	}

	public abstract Future<Void> handleSuccess(RoutingContext context);

	protected void handleFailure(RoutingContext context) {
		this.defaultFailureHandler.handle(context);
	}
}
