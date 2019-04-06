package com.yjl.vertx.base.dao.adaptor;

import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.ext.sql.UpdateResult;

public class InsertIntDaoAdaptor extends AbstractDaoAdaptor<UpdateResult, Integer> {
	@Override
	public SqlOperation matchOperation() {
		return SqlOperation.INSERT;
	}

	@Override
	public Integer adapt(UpdateResult dbResult) {
		return dbResult.getUpdated();
	}
}
