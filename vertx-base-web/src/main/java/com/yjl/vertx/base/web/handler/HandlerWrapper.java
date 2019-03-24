package com.yjl.vertx.base.web.handler;

import com.yjl.vertx.base.web.enumeration.RouteMethod;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class HandlerWrapper {

	private String url;

	private RouteMethod method;

	private boolean regexp = false;

	private String descript;

	private boolean autoHandleError = true;

	private Handler<RoutingContext> handler;

	private int order;
}
