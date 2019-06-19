package com.yjl.vertx.base.auth.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.auth.dto.AuthorizeResult;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.util.StringUtil;
import com.yjl.vertx.base.redis.component.RedisFutureComponent;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.redis.client.Response;

public class UsiAuthorizeComponent implements AuthorizeComponentIf {
    
    @Inject
    private JWTAuth jwtAuth;
    
    @Inject(optional = true)
    @Config("auth.token.headerName")
    private String tokenHeaderName = "token";
    
    @Inject(optional = true)
    @Config("auth.token.redisKey")
    private String redisKey = "sa_authority";
    
    @Inject(optional = true)
    @Config("auth.token.expired")
    private int expired = 600;
    
    @Inject
    private RedisFutureComponent redisFutureComponent;
    
    @Override
    public Future<AuthorizeResult> authorize(String url, JsonObject headers, JsonObject params) {
        Future<AuthorizeResult> future = Future.future();
        String token = headers.getString(this.tokenHeaderName);
        if (StringUtil.isBlank(token)) {
            return Future.succeededFuture(new AuthorizeResult().result(false).resCd(-5));
        }
        Future<User> userFuture = Future.future();
        this.jwtAuth.authenticate(new JsonObject().put("jwt", token), userFuture);
//            asyncResult -> {
//                if (asyncResult.failed()) {
//                    userRedisFuture.fail(asyncResult.cause());
//                } else {
//                    userRedisFuture.complete(asyncResult.result().principal().getString("account"));
//                }
//            }
//        );
        userFuture.setHandler(userAsyncResult -> {
            if (userAsyncResult.failed()) {
                future.complete(new AuthorizeResult().result(false).resCd(-5));
                return;
            }
            String account = userAsyncResult.result().principal().getString("account");
            this.redisFutureComponent.hget(this.redisKey, account).setHandler(responseAsyncResult -> {
                if (responseAsyncResult.failed()) {
                    future.complete(new AuthorizeResult().result(false).resCd(-9));
                    return;
                }
                Response response = responseAsyncResult.result();
                if (response == null || response.size() == 0) {
                    future.complete(new AuthorizeResult().result(false).resCd(-4));
                    return;
                }
                JsonObject resData = JsonObject.mapFrom(response.toBuffer());
                if (resData.getLong("lastAccessTime", 0L) + this.expired * 1000 < System.currentTimeMillis()) {
                    future.complete(new AuthorizeResult().result(false).resCd(-1));
                    return;
                } else {
                    resData.put("lastAccessTime", System.currentTimeMillis());
                    this.redisFutureComponent.hsetnx(this.redisKey, account, resData.toString())
                        .setHandler(responseAsyncResult2 -> {});
                }
                boolean authorize = resData.getJsonArray("userMenus", new JsonArray()).stream()
                    .map(String::valueOf).anyMatch(url::startsWith);
                future.complete(new AuthorizeResult().result(authorize).resCd(authorize ? 1 : -3));
            });
        });
        return future;
//        this.jwtAuth.authenticate(new JsonObject().put("jwt", headers.getString(this.tokenHeaderName, "")),
//            asyncResult -> future.complete(asyncResult.succeeded() && asyncResult.result().principal()
//                .getJsonArray("userMenus").stream().map(String::valueOf).anyMatch(url::startsWith))
//        );
//        return future;
    }
}
