package com.yjl.vertx.base.auth.component;

import com.yjl.vertx.base.auth.dto.AuthorizeResult;
import com.yjl.vertx.base.com.exception.FrameworkException;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public class AuthorizeCompleteFailComponent implements AuthorizeCompleteListener {
    
    @Override
    public Future<Void> authorizeComplete(RoutingContext context, AuthorizeResult result) {
        if (!result.isSuccess()) {
            return Future.failedFuture(new FrameworkException().message("authorize failed").errCode(result.getResCd()));
        } else {
            return Future.succeededFuture();
        }
    }
}
