package com.yjl.vertx.base.dao.executor;

import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.function.ThreeParamFunction;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.BiFunction;

public class SelectCommandExecutor extends AbstractCommandExecutor<ResultSet> {

	@Override
	public SqlOperation getSqlOperation() {
		return SqlOperation.SELECT;
	}

	@Override
	protected Object adaptReturnValue(ResultSet dbResult, Type returnType) {
		if (!(returnType instanceof ParameterizedType)) {
			throw new FrameworkException().message("method return type is not ParameterizedType: " + returnType.getTypeName());
		}
		ParameterizedType parameterizedType = ReflectionsUtil.autoCast(returnType);
		return null;
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
