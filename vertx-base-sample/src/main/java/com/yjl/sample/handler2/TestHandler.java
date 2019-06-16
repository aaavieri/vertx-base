package com.yjl.sample.handler2;

import com.google.inject.Inject;
import com.yjl.sample.mapper.WxUserMapper;
import com.yjl.vertx.base.com.util.JsonUtil;
import com.yjl.vertx.base.web.anno.component.RestRouteMapping;
import com.yjl.vertx.base.web.anno.component.RestRouteV2Handler;
import com.yjl.vertx.base.web.handler.BaseRouteV2Handler;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@RestRouteV2Handler({@RestRouteMapping(value = "/test/1", method = HttpMethod.POST), @RestRouteMapping("/test/2")})
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
				JsonObject retData = new JsonObject();
				context.request().params().forEach(entry -> retData.put(entry.getKey(), entry.getValue()));
				context.request().formAttributes().forEach(entry -> retData.put(entry.getKey(), entry.getValue()));
				if (JsonUtil.isJson(context.getBodyAsString())) {
					context.getBodyAsJson().getMap().forEach(retData::put);
				}
				context.response().end(retData.put("userInfo", jsonObject).toBuffer());
				return Future.succeededFuture();
			});
	}
}
