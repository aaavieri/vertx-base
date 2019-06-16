package com.yjl.vertx.base.web.handler;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class HandlerWrapper {

	private String url;

	private HttpMethod method;

	private boolean regexp = false;

	private String descript;

	private boolean autoHandleError = true;

	private Handler<RoutingContext> handler;
	
	private Class<?> handlerClass;
	
	private String handlerMethod;

	private int order;
}
