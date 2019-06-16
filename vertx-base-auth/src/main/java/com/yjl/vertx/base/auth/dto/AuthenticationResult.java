package com.yjl.vertx.base.auth.dto;

import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class AuthenticationResult {
    private boolean result;
    private JsonObject userInfo;
}
