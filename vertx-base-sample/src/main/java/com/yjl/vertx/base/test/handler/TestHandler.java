package com.yjl.vertx.base.test.handler;

import com.google.inject.Inject;
import com.yjl.vertx.base.web.anno.component.RestRouteV1Handler;
import com.yjl.vertx.base.web.anno.component.RestRouteMapping;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@RestRouteV1Handler("/test")
public class TestHandler {

	@Inject
	private Vertx vertx;

	@RestRouteMapping("first")
	public Handler<RoutingContext> first() {
		return routingContext -> {
			routingContext.response().end(new JsonObject().put("first", true)
					.put("deploymentIDs", this.vertx.deploymentIDs()).toBuffer());
		};
	}
}
