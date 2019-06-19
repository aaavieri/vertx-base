package com.yjl.vertx.base.auth.component;

import com.yjl.vertx.base.auth.dto.AuthorizeResult;
import com.yjl.vertx.base.com.exception.FrameworkException;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public class AuthorizeCompleteFailComponent implements AuthorizeCompleteListener {
    
    @Override
    public Future<Void> authorizeComplete(RoutingContext context, AuthorizeResult result) {
        if (!result.result()) {
            return Future.failedFuture(new FrameworkException().message("authorize failed").errCode(-1));
        } else {
            return Future.succeededFuture();
        }
    }
}
