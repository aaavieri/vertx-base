package com.yjl.vertx.base.autoroute.util;

import com.yjl.vertx.base.autoroute.anno.AutoRouteIf;
import com.yjl.vertx.base.autoroute.anno.AutoRouteIfMethod;
import com.yjl.vertx.base.com.anno.Order;
import com.yjl.vertx.base.com.util.JsonUtil;
import com.yjl.vertx.base.com.util.StringUtil;
import com.yjl.vertx.base.web.handler.HandlerWrapper;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AutoRouteUtil {

	public static JsonObject getRequestParam(RoutingContext context) {
		JsonObject jsonObject = new JsonObject();
		Consumer<Map.Entry<String, ?>> entryConsumer = entry -> jsonObject.put(entry.getKey(), entry.getValue());
		context.request().params().forEach(entryConsumer);
		context.request().formAttributes().forEach(entryConsumer);
		String body = context.getBodyAsString();
		if (JsonUtil.isJson(body)) {
			context.getBodyAsJson().forEach(entryConsumer);
		}
		return jsonObject;
	}

	public static HandlerWrapper getHandlerWrapper(Method method, Handler<RoutingContext> handler) {
		Class<?> clazz = method.getDeclaringClass();
		AutoRouteIf autoRouteIf = clazz.getAnnotation(AutoRouteIf.class);
		AutoRouteIfMethod autoRouteIfMethod = method.getAnnotation(AutoRouteIfMethod.class);
		String parentUrl = autoRouteIf == null || StringUtil.isBlank(autoRouteIf.value())
			? clazz.getSimpleName() : autoRouteIf.value();
		String childUrl = autoRouteIfMethod == null || StringUtil.isBlank(autoRouteIfMethod.value())
			? method.getName() : autoRouteIfMethod.value();
		Order order = method.getAnnotation(Order.class);
		return new HandlerWrapper().order(order == null ? Integer.MAX_VALUE : order.value())
			.handler(handler).url(StringUtil.concatPath(parentUrl, childUrl))
            .handlerClass(clazz).handlerMethod(method.getName())
			.method(autoRouteIfMethod != null ? autoRouteIfMethod.route() : HttpMethod.GET)
			.regexp(false).autoHandleError(true);
	}
}
