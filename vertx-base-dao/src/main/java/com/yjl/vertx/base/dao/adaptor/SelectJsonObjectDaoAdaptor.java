package com.yjl.vertx.base.dao.adaptor;

import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.List;

public class SelectJsonObjectDaoAdaptor extends AbstractDaoAdaptor<ResultSet, JsonObject> {
	@Override
	public SqlOperation matchOperation() {
		return SqlOperation.SELECT;
	}

	@Override
	public JsonObject adapt(ResultSet resultSet) {
		List<JsonObject> dataList = resultSet.getRows();
		if (dataList.size() == 0) {
			return null;
		} else {
			return dataList.get(0);
		}
	}
}
