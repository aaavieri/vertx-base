package com.yjl.vertx.base.test.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.component.Component;
import com.yjl.vertx.base.test.dbmapper.ClientMapper;
import com.yjl.vertx.base.test.dbmapper.WxChannelMapper;
import com.yjl.vertx.base.test.dbmapper.WxUserMapper;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;

@Component
public class TestService {

	@Inject(optional = true)
	@Getter
	private Vertx vertx;
	
	@Inject
    private ClientMapper clientMapper;
	@Inject
    private WxUserMapper wxUserMapper;
	@Inject
    private WxChannelMapper wxChannelMapper;
	
	public void test(int clientId, RoutingContext routingContext) {
	    Future<JsonObject> clientInfoFuture = this.clientMapper.findByClientId(clientId);
	    clientInfoFuture.setHandler(as -> {
	        if (as.succeeded()) {
	            String openId = as.result().getString("openId");
                String appId = as.result().getString("appId");
	            Future<JsonObject> userInfoFuture = this.wxUserMapper.getWxUser(appId, openId);
                userInfoFuture.setHandler(as2 -> {
                    if (as2.succeeded()) {
                        Future<JsonObject> channelFuture = this.wxChannelMapper.getChannelInfo(appId);
                        channelFuture.setHandler(as3 -> {
                            if (as3.succeeded()) {
                                routingContext.response().end(new JsonObject().mergeIn(as.result(), true)
                                    .mergeIn(as2.result(), true)
                                    .mergeIn(as3.result(), true).toBuffer());
                            }
                        });
                    }
                });
            }
        });
    }
    
    public void test2(int clientId, RoutingContext routingContext) {
	    final JsonObject data = new JsonObject();
        this.clientMapper.findByClientId(clientId).compose(clientInfo -> {
            data.mergeIn(clientInfo, true);
            String openId = clientInfo.getString("openId");
            String appId = clientInfo.getString("appId");
            return this.wxUserMapper.getWxUser(appId, openId);
        }).compose(userInfo -> {
            data.mergeIn(userInfo, true);
            return this.wxChannelMapper.getChannelInfo(data.getString("appId"));
        }).setHandler(as -> {
            if (as.succeeded()) {
                data.mergeIn(as.result(), true);
                routingContext.response().end(data.toBuffer());
            }
        });
    }
}
