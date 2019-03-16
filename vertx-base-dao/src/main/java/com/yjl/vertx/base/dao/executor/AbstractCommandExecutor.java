package com.yjl.vertx.base.dao.executor;

import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.function.ThreeParamFunction;
import com.yjl.vertx.base.dao.command.SqlCommand;
import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.function.BiFunction;

public abstract class AbstractCommandExecutor<T> {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	public Future<T> execute(SQLConnection connection, SqlCommand command) {
		Future<T> future = Future.future();
		Handler<AsyncResult<T>> callback = as -> {
			if (as.failed()) {
				logger.error(as.cause().getMessage(), as.cause());
				throw new FrameworkException(as.cause());
			} else {
				future.complete(as.result());
			}
		};
		try	{
			if (command.withParams()) {
				this.getWithParamSqlFunction(connection).apply(command.sql(), command.params(), callback);
			} else {
				this.getSqlFunction(connection).apply(command.sql(), callback);
			}
		} catch (Throwable t) {
			throw new FrameworkException(t);
		}
		return future;
	}

	public abstract SqlOperation getSqlOperation();

	protected abstract Object adaptReturnValue(T dbResult, Type returnType);

	protected abstract ThreeParamFunction<String, JsonArray, Handler<AsyncResult<T>>, SQLConnection> getWithParamSqlFunction(SQLConnection connection);

	protected abstract BiFunction<String, Handler<AsyncResult<T>>, SQLConnection> getSqlFunction(SQLConnection connection);
}
