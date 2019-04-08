package com.yjl.vertx.base.com.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@Setter
@NoArgsConstructor
public class FrameworkException extends RuntimeException {

	public FrameworkException(Throwable throwable) {
		super(throwable);
	}

	private int errCode;

	private String message;
}
