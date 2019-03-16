package com.yjl.vertx.base.test.handler;

import com.google.inject.Inject;
import com.yjl.vertx.base.test.component.Test2Service;
import com.yjl.vertx.base.web.anno.component.RestRouteV1Handler;
import com.yjl.vertx.base.web.anno.component.RestRouteMapping;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@RestRouteV1Handler("/test2")
public class Test2Handler {

	@Inject
	private Test2Service test2Service;

	@RestRouteMapping("second")
	public Handler<RoutingContext> second() {
		return routingContext -> {
			test2Service.test();
			routingContext.response().end(new JsonObject().put("second", true)
					.put("test2Service", this.test2Service.toString()).toBuffer());
		};
	}
}
