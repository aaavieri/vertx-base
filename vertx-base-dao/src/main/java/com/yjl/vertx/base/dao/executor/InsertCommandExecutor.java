package com.yjl.vertx.base.dao.executor;

import com.yjl.vertx.base.com.function.ThreeParamFunction;
import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

import java.lang.reflect.Type;
import java.util.function.BiFunction;

public class InsertCommandExecutor extends AbstractCommandExecutor<UpdateResult> {

	@Override
	public SqlOperation getSqlOperation() {
		return SqlOperation.INSERT;
	}

	@Override
	protected Object adaptReturnValue(UpdateResult dbResult, Type returnType) {
		return null;
	}

	@Override
	protected ThreeParamFunction<String, JsonArray, Handler<AsyncResult<UpdateResult>>, SQLConnection> getWithParamSqlFunction(SQLConnection connection) {
		return connection::updateWithParams;
	}

	@Override
	protected BiFunction<String, Handler<AsyncResult<UpdateResult>>, SQLConnection> getSqlFunction(SQLConnection connection) {
		return connection::update;
	}
}
