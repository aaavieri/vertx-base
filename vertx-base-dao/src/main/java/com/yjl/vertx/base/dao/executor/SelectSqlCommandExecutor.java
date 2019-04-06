package com.yjl.vertx.base.dao.executor;

import com.yjl.vertx.base.com.function.ThreeParamFunction;
import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

import java.util.function.BiFunction;

public class SelectSqlCommandExecutor extends AbstractSqlCommandExecutor<ResultSet> {

	@Override
	public SqlOperation getSqlOperation() {
		return SqlOperation.SELECT;
	}

	@Override
	protected ThreeParamFunction<String, JsonArray, Handler<AsyncResult<ResultSet>>, SQLConnection> getWithParamSqlFunction(SQLConnection connection) {
		return connection::queryWithParams;
	}

	@Override
	protected BiFunction<String, Handler<AsyncResult<ResultSet>>, SQLConnection> getSqlFunction(SQLConnection connection) {
		return connection::query;
	}
}
