package com.yjl.vertx.base.web.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@Setter
@NoArgsConstructor
public class ApplicationException extends RuntimeException {

    public ApplicationException(Throwable throwable) {
        super(throwable);
    }

    private int errCode;

    private String message;
}
