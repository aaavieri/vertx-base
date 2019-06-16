package com.yjl.vertx.base.auth.component;

import com.yjl.vertx.base.auth.dto.AuthenticationResult;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.stream.Collectors;

public class UsiAuthenticationSucceedComponent implements AuthenticationCompleteListener {
    @Override
    public Future<Void> authenticateComplete(RoutingContext context, AuthenticationResult result) {
        JsonObject retUserInfo = new JsonObject().mergeIn(result.userInfo(), true);
        retUserInfo.remove("password");
        List<String> menus = retUserInfo.getJsonArray("userMenus").stream()
            .map(menu -> ReflectionsUtil.<JsonObject>autoCast(menu).getString("server_uri"))
            .collect(Collectors.toList());
        retUserInfo.put("userMenus", menus);
        context.response().write(retUserInfo.toBuffer());
        return Future.succeededFuture();
    }
}
