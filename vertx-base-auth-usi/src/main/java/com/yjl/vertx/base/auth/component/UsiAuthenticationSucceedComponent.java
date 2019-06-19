package com.yjl.vertx.base.auth.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.auth.dto.AuthenticationResult;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.redis.component.RedisFutureComponent;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.stream.Collectors;

public class UsiAuthenticationSucceedComponent implements AuthenticationCompleteListener {
    
    @Inject(optional = true)
    @Config("auth.token.redisKey")
    private String redisKey = "sa_authority";
    
    @Inject
    private RedisFutureComponent redisFutureComponent;
    
    @Override
    public Future<Void> authenticateComplete(RoutingContext context, AuthenticationResult result) {
        JsonObject retUserInfo = new JsonObject().mergeIn(result.userInfo(), true);
        retUserInfo.remove("password");
        List<String> menus = retUserInfo.getJsonArray("userMenus").stream()
            .map(menu -> ReflectionsUtil.<JsonObject>autoCast(menu).getString("server_uri"))
            .collect(Collectors.toList());
        String redisData = new JsonObject().put("userMenus", menus).put("lastAccessTime", System.currentTimeMillis()).toString();
        context.response().write(retUserInfo.toBuffer());
        return this.redisFutureComponent.hsetnx(this.redisKey, retUserInfo.getString("account"), redisData)
            .compose(response -> Future.succeededFuture());
    }
}
