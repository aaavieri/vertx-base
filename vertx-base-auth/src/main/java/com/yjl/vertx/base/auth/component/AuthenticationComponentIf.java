package com.yjl.vertx.base.auth.component;

import com.yjl.vertx.base.auth.dto.AuthenticationResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface AuthenticationComponentIf {
    Future<AuthenticationResult> authenticate(JsonObject headers, JsonObject params);
}
