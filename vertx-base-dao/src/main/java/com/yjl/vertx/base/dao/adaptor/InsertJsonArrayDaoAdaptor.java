package com.yjl.vertx.base.dao.adaptor;

import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.UpdateResult;

public class InsertJsonArrayDaoAdaptor extends AbstractDaoAdaptor<UpdateResult, JsonArray> {
	@Override
	public SqlOperation matchOperation() {
		return SqlOperation.INSERT;
	}

	@Override
	public JsonArray adapt(UpdateResult dbResult) {
		return dbResult.getKeys();
	}
}
