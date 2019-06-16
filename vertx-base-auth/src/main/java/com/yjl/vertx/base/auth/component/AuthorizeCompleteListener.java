package com.yjl.vertx.base.auth.component;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public interface AuthorizeCompleteListener {
    Future<Void> authorizeComplete(RoutingContext context, boolean result);
}
