package com.yjl.sample.handler2;

import com.yjl.vertx.base.web.anno.component.RestRouteMapping;
import com.yjl.vertx.base.web.anno.component.RestRouteV2Handler;
import com.yjl.vertx.base.web.handler.BaseRouteV2Handler;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@RestRouteV2Handler(@RestRouteMapping("/test3/1"))
public class Test3Handler extends BaseRouteV2Handler {
    @Override
    public Future<Void> handleSuccess(RoutingContext context) {
        context.response().end(new JsonObject().put("ok", true).toBuffer());
        return Future.succeededFuture();
    }
}
