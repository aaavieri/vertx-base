package com.yjl.vertx.base.web.factory.handler;

import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.web.exception.ApplicationException;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FailureHandlerFactory {

	public static Handler<RoutingContext> getDefault() {
		return routingContext -> {
			if (routingContext.failure() == null) {
				routingContext.response().end(new JsonObject().put("errMsg", "unknown error occurred").put("success", false).put("errCode", 9).toBuffer());
			} else if (routingContext.failure() instanceof ApplicationException) {
				routingContext.failure().printStackTrace();
				ApplicationException e = ReflectionsUtil.autoCast(routingContext.failure());
				routingContext.response().end(new JsonObject().put("errMsg", e.message()).put("success", false).put("errCode", e.errCode()).toBuffer());
			} else if (routingContext.failure() instanceof FrameworkException) {
				routingContext.failure().printStackTrace();
				FrameworkException e = ReflectionsUtil.autoCast(routingContext.failure());
				routingContext.response().end(new JsonObject().put("errMsg", e.message()).put("success", false).put("errCode", e.errCode()).toBuffer());
			} else {
				routingContext.failure().printStackTrace();
				routingContext.response().end(new JsonObject().put("errMsg", routingContext.failure().getMessage()).put("success", false).put("errCode",  -1).toBuffer());
			}
		};
	}
}
