package com.yjl.vertx.base.dao.adaptor;

import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.List;

public class SelectListJsonObjectDaoAdaptor extends AbstractDaoAdaptor<ResultSet, List<JsonObject>> {

	@Override
	public SqlOperation matchOperation() {
		return SqlOperation.SELECT;
	}

	@Override
	public List<JsonObject> adapt(ResultSet resultSet) {
		return resultSet.getRows();
	}
}
