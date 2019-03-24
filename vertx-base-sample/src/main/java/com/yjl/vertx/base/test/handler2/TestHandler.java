package com.yjl.vertx.base.test.handler2;

import com.google.inject.Inject;
import com.yjl.vertx.base.test.dbmapper.WxUserMapper;
import com.yjl.vertx.base.web.anno.component.RestRouteMapping;
import com.yjl.vertx.base.web.anno.component.RestRouteV2Handler;
import com.yjl.vertx.base.web.handler.BaseRouteV2Handler;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@RestRouteV2Handler({@RestRouteMapping("/test/1"), @RestRouteMapping("/test/2")})
public class TestHandler extends BaseRouteV2Handler {

	@Inject
	private Vertx vertx;

	@Inject
	private WxUserMapper wxUserMapper;

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
		return this.wxUserMapper.getWxUser("wxbf7f0a968c9d7f90", "ozYIb5EhFQDNuxjwfCFStOfFrHdY")
			.compose(jsonObject -> {
				Future<Void> future = Future.future();
				context.response().end(new JsonObject().put("userInfo", jsonObject).toBuffer());
				future.complete();
				return future;
			});
	}
}
