package com.yjl.vertx.base.dao.factory.component;

import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.SQLClient;

public class MysqlSqlClientFactory extends BaseSqlClientFactory {

	@Override
	protected SQLClient getSqlClient() {
		return MySQLClient.createNonShared(this.vertx, this.dbconfig);
	}
}
