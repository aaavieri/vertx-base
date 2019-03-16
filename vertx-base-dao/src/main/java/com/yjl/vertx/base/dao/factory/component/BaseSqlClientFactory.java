package com.yjl.vertx.base.dao.factory.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;

public abstract class BaseSqlClientFactory extends BaseAnnotationComponentFactory {

	@Inject
	@Config("databaseConfig")
	protected JsonObject dbconfig;

	@Inject
	protected Vertx vertx;

	@Override
	public void configure() {
		this.bind(SQLClient.class).toInstance(this.getSqlClient());
	}

	protected abstract SQLClient getSqlClient();
}
