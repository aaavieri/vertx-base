package com.yjl.vertx.base.test.handler;

import com.google.inject.Inject;
import com.yjl.vertx.base.test.component.Test2Service;
import com.yjl.vertx.base.test.dbmapper.WxUserMapper;
import com.yjl.vertx.base.web.anno.component.RestRouteMapping;
import com.yjl.vertx.base.web.anno.component.RestRouteV1Handler;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@RestRouteV1Handler("/test2")
public class Test2Handler {

	@Inject
	private Test2Service test2Service;
	
	@Inject
    private WxUserMapper wxUserMapper;

	@RestRouteMapping("second")
	public Handler<RoutingContext> second() {
		return routingContext -> {
			test2Service.test();
            Future<JsonObject> future = this.wxUserMapper.getWxUser("a", "b");
            future.setHandler(as -> {
                if (as.succeeded()) {
                    routingContext.response().end(as.result().put("second", true).toBuffer());
                } else {
                    routingContext.response().end(new JsonObject().put("cause", as.cause()).toBuffer());
                }
            });
		};
	}
}
