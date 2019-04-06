package com.yjl.vertx.base.web.factory.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;

public class HttpServerFactory extends BaseAnnotationComponentFactory {

	protected Router router;

	protected HttpServer server;

	@Inject
	protected Vertx vertx;

	@Inject
	@Config("app.port")
	private int port = this.defaultPort();

	@Override
	public void configure() {
		this.server = vertx.createHttpServer();
		this.router = Router.router(this.vertx);
		this.router.route().handler(CookieHandler.create())
			.handler(BodyHandler.create())
			.produces("application/json;charset=UTF-8")
			.handler(ResponseContentTypeHandler.create());
		this.bind(HttpServer.class).toInstance(this.server);
		this.bind(Router.class).toInstance(this.router);
	}

	public void afterConfigure() {
		this.server.requestHandler(this.router).listen(this.getPort());
	}

	protected int getPort() {
		return this.port;
	}

	protected int defaultPort() {
		return 8080;
	}
}
