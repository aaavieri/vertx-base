package com.yjl.vertx.base.auth.component;

import com.yjl.vertx.base.auth.dto.AuthenticationResult;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public interface AuthenticationCompleteListener {
    Future<Void> authenticateComplete(RoutingContext context, AuthenticationResult result);
}
