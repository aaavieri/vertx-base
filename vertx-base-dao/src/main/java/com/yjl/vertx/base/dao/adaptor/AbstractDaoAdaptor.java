package com.yjl.vertx.base.dao.adaptor;

import com.yjl.vertx.base.com.function.AdaptorFunction;
import com.yjl.vertx.base.dao.command.SqlCommand;
import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;

import java.lang.reflect.Type;

public abstract class AbstractDaoAdaptor<T, R> implements AdaptorFunction<T, R> {
	public abstract SqlOperation matchOperation();

	public boolean isMatch(SqlOperation operation, Type returnType) {
		return this.isMatch(this.getInputType(operation), returnType);
	}

	protected Type getInputType(SqlOperation operation) {
		if (operation.equals(SqlOperation.SELECT)) {
			return ResultSet.class;
		} else {
			return UpdateResult.class;
		}
	}
}
