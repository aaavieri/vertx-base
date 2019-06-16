package com.yjl.vertx.base.auth.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.auth.dto.AuthenticationResult;
import com.yjl.vertx.base.com.anno.component.Config;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;

public class UsiTokenGeneratorComponent implements AuthenticationCompleteListener {
    
    @Inject
    private Vertx vertx;
    
    @Inject
    private JWTAuth jwtAuth;
    
    @Inject(optional = true)
    @Config("auth.token.headerName")
    private String tokenHeaderName = "token";
    
    @Override
    public Future<Void> authenticateComplete(RoutingContext context, AuthenticationResult result) {
        Future<Void> future = Future.future();
        this.vertx.executeBlocking(Void -> {
            String token = this.jwtAuth.generateToken(result.userInfo());
            context.response().putHeader(this.tokenHeaderName, token);
        }, asyncResult -> {
            if (asyncResult.succeeded()) {
                future.complete();
            } else {
                future.fail(asyncResult.cause());
            }
        });
        return future;
    }
}