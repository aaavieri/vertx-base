package com.yjl.sample.handler2;

import com.google.inject.Inject;
import com.yjl.sample.client.LocalWebClient;
import com.yjl.sample.mapper.WxUserMapper;
import com.yjl.vertx.base.web.anno.component.RestRouteMapping;
import com.yjl.vertx.base.web.anno.component.RestRouteV2Handler;
import com.yjl.vertx.base.web.handler.BaseRouteV2Handler;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@RestRouteV2Handler(@RestRouteMapping("/test2/1"))
public class Test2Handler extends BaseRouteV2Handler {

	@Inject
	private Vertx vertx;

	@Inject
	private WxUserMapper wxUserMapper;

	@Inject
	private LocalWebClient localWebClient;

//	@Override
//	public void handle(RoutingContext context) {
//		this.wxUserMapper.getWxUser("wxbf7f0a968c9d7f90", "ozYIb5EhFQDNuxjwfCFStOfFrHdY")
//			.setHandler(as -> {
//				if (as.failed()) {
//					throw new ApplicationException(as.cause());
//				}
//				context.response().end(new JsonObject().put("userInfo", as.result()).toBuffer());
//			});
//	}

	public Future<Void> handleSuccess(RoutingContext context) {
		return this.localWebClient.testFirst("2", "3", 5)
			.compose(jsonObject -> {
				context.response().end(new JsonObject().put("result", jsonObject).toBuffer());
				return Future.succeededFuture();
			});
	}
}
