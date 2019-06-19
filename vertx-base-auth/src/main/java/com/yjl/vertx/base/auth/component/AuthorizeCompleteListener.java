package com.yjl.vertx.base.auth.component;

import com.yjl.vertx.base.auth.dto.AuthorizeResult;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public interface AuthorizeCompleteListener {
    Future<Void> authorizeComplete(RoutingContext context, AuthorizeResult result);
}
