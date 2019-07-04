package com.yjl.vertx.base.auth.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class AuthorizeResult {

    private boolean success;
    private int resCd;
    private String message;
}
