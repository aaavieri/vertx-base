package com.yjl.vertx.base.dao.adaptor;

import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.dao.command.SqlCommand;
import com.yjl.vertx.base.dao.enumeration.SqlOperation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractAdaptor<T, U> {
	public abstract SqlOperation matchOperation();

	public abstract U adapt(T t);

	public boolean isMatch(SqlCommand sqlCommand) {
		Type outputType = ReflectionsUtil.<ParameterizedType>autoCast(this.getClass().getGenericSuperclass())
			.getActualTypeArguments()[1];
		return this.matchOperation().equals(sqlCommand.sqlOperation())
			&& ReflectionsUtil.compareType(outputType, sqlCommand.returnType(), false);
	}
}
