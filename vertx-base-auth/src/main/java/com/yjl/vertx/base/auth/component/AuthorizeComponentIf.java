package com.yjl.vertx.base.auth.component;

import com.yjl.vertx.base.auth.dto.AuthorizeResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface AuthorizeComponentIf {
    Future<AuthorizeResult> authorize(String url, JsonObject headers, JsonObject params);
}
