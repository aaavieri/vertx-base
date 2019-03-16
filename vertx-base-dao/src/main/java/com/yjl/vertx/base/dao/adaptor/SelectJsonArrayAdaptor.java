package com.yjl.vertx.base.dao.adaptor;

import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;

public class SelectJsonArrayAdaptor extends AbstractAdaptor<ResultSet, JsonArray> {
	@Override
	public SqlOperation matchOperation() {
		return SqlOperation.SELECT;
	}

	@Override
	public JsonArray adapt(ResultSet resultSet) {
		return resultSet.getOutput();
	}
}
