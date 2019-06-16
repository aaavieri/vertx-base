package com.yjl.vertx.base.auth.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.component.Config;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;

public class UsiAuthorizeComponent implements AuthorizeComponentIf {
    
    @Inject
    private JWTAuth jwtAuth;
    
    @Inject(optional = true)
    @Config("auth.token.headerName")
    private String tokenHeaderName = "token";
    
    @Override
    public Future<Boolean> authorize(String url, JsonObject headers, JsonObject params) {
        Future<Boolean> future = Future.future();
        this.jwtAuth.authenticate(new JsonObject().put("jwt", headers.getString(this.tokenHeaderName, "")),
            asyncResult -> future.complete(asyncResult.succeeded() && asyncResult.result().principal()
                .getJsonArray("userMenus").stream().map(String::valueOf).anyMatch(url::startsWith))
        );
        return future;
    }
}
