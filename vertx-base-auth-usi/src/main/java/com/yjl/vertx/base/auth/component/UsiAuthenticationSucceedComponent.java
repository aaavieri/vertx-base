package com.yjl.vertx.base.auth.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.auth.dto.AuthenticationResult;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.redis.component.RedisFutureComponent;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;

public class UsiAuthenticationSucceedComponent implements AuthenticationCompleteListener {
    
    @Inject(optional = true)
    @Config("auth.token.redisKey")
    private String redisKey = "sa_authority";

    @Inject
    private JWTAuth jwtAuth;

    @Inject(optional = true)
    @Config("auth.token.headerName")
    private String tokenHeaderName = "token";
    
    @Inject
    private RedisFutureComponent redisFutureComponent;
    
    @Override
    public Future<Void> authenticateComplete(RoutingContext context, AuthenticationResult result) {
        JsonObject retUserInfo = new JsonObject().mergeIn(result.userInfo(), true);
        retUserInfo.remove("password");
        if (result.result()) {
//        List<String> menus = retUserInfo.getJsonArray("userMenus").stream()
//            .map(menu -> ReflectionsUtil.<JsonObject>autoCast(menu).getString("server_uri"))
//            .collect(Collectors.toList());
            String redisData = new JsonObject().put("userMenus", retUserInfo.remove("userMenus"))
                .put("lastAccessTime", System.currentTimeMillis()).toString();
            JsonObject tokenJson = new JsonObject().mergeIn(result.userInfo(), true);
            tokenJson.remove("userMenus");
            context.response().write(retUserInfo.put(this.tokenHeaderName, this.jwtAuth.generateToken(tokenJson)).toBuffer());
            return this.redisFutureComponent.hset(this.redisKey, retUserInfo.getString("account"), redisData)
                .compose(response -> Future.succeededFuture());
        } else {
            context.response().write(retUserInfo.toBuffer());
            return Future.succeededFuture();
        }
    }
}
