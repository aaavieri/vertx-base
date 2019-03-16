package com.yjl.vertx.base.dao.adaptor;

import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.UpdateResult;

public class DeleteJsonArrayAdaptor extends AbstractAdaptor<UpdateResult, JsonArray> {
	@Override
	public SqlOperation matchOperation() {
		return SqlOperation.DELETE;
	}

	@Override
	public JsonArray adapt(UpdateResult dbResult) {
		return dbResult.getKeys();
	}
}
