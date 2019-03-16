package com.yjl.vertx.base.test.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.component.Component;
import io.vertx.core.Vertx;
import lombok.Getter;

@Component
public class TestService {

	@Inject(optional = true)
	@Getter
	private Vertx vertx;
}
