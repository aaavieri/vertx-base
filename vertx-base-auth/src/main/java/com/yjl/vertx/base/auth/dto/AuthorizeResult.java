package com.yjl.vertx.base.auth.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class AuthorizeResult {
    private boolean result;
    private int resCd;
    private String message;
}
