package com.yjl.vertx.base.auth.component;

import com.yjl.vertx.base.auth.dto.AuthorizeResult;
import com.yjl.vertx.base.web.exception.ApplicationWithDataException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class UsiAuthorizeCompleteFailComponent implements AuthorizeCompleteListener {
    
    @Override
    public Future<Void> authorizeComplete(RoutingContext context, AuthorizeResult result) {
        if (!result.isSuccess()) {
            return Future.failedFuture(new ApplicationWithDataException().errorData(
                JsonObject.mapFrom(result.setMessage("authorize failed"))));
        } else {
            return Future.succeededFuture();
        }
    }
}
