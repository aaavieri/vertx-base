package com.yjl.vertx.base.test.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.component.Component;

@Component
public class Test2Service {

	@Inject
	private TestService testService;

	public void test() {
		System.out.println(this.testService);
		System.out.println(this.testService.getVertx());
	}
}
