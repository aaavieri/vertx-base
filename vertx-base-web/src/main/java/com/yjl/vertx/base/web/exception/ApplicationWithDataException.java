package com.yjl.vertx.base.web.exception;

import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@Setter
@NoArgsConstructor
public class ApplicationWithDataException extends RuntimeException {

    public ApplicationWithDataException(Throwable throwable) {
        super(throwable);
    }

    private JsonObject errorData;
}
