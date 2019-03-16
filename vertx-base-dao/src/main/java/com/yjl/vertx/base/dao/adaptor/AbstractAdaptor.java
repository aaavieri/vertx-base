package com.yjl.vertx.base.dao.adaptor;

import com.yjl.vertx.base.dao.enumeration.SqlOperation;

public abstract class AbstractAdaptor<T, U> {
	public abstract SqlOperation matchOperation();

	public abstract U adapt(T t);
}
