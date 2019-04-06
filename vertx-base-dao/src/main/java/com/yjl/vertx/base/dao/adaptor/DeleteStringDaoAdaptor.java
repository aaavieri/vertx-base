package com.yjl.vertx.base.dao.adaptor;

import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.UpdateResult;

public class DeleteStringDaoAdaptor extends AbstractDaoAdaptor<UpdateResult, String> {
	@Override
	public SqlOperation matchOperation() {
		return SqlOperation.DELETE;
	}

	@Override
	public String adapt(UpdateResult dbResult) {
		JsonArray keys = dbResult.getKeys();
		return keys.size() == 0 ? null : keys.getString(0);
	}
}
