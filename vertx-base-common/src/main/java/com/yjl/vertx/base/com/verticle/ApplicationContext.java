package com.yjl.vertx.base.com.verticle;

import com.google.inject.Injector;

public class ApplicationContext {

	private static Injector context = null;

	public static Injector getContext() {
		return context;
	}

	static void setContext(Injector injector) {
		context = injector;
	}
}
