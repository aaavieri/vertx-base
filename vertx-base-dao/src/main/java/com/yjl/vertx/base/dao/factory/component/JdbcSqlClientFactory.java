package com.yjl.vertx.base.dao.factory.component;

import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;

public class JdbcSqlClientFactory extends BaseSqlClientFactory {
	@Override
	protected SQLClient getSqlClient() {
		return JDBCClient.createNonShared(this.vertx, this.dbconfig);
	}
}
