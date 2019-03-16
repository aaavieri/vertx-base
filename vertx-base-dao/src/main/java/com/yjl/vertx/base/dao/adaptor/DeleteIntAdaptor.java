package com.yjl.vertx.base.dao.adaptor;

import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.ext.sql.UpdateResult;

public class DeleteIntAdaptor extends AbstractAdaptor<UpdateResult, Integer> {
	@Override
	public SqlOperation matchOperation() {
		return SqlOperation.DELETE;
	}

	@Override
	public Integer adapt(UpdateResult dbResult) {
		return dbResult.getUpdated();
	}
}
