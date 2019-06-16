package com.yjl.vertx.base.auth.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface AuthorizeComponentIf {
    Future<Boolean> authorize(String url, JsonObject headers, JsonObject params);
}
