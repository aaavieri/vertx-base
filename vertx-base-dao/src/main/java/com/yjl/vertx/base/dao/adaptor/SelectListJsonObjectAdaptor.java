package com.yjl.vertx.base.dao.adaptor;

import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectListJsonObjectAdaptor extends AbstractAdaptor<ResultSet, List<JsonObject>> {

	@Override
	public SqlOperation matchOperation() {
		return SqlOperation.SELECT;
	}

	@Override
	public List<JsonObject> adapt(ResultSet resultSet) {
		return resultSet.getRows();
	}
}
