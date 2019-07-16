package com.yjl.vertx.base.auth.dto;

import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class AuthenticationResult {
    private boolean success;
    private JsonObject userInfo;
    private int resCd;
    private String message;
}
