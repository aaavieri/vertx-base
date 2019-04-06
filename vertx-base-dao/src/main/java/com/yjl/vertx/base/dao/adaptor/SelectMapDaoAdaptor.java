package com.yjl.vertx.base.dao.adaptor;

import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.List;
import java.util.Map;

public class SelectMapDaoAdaptor extends AbstractDaoAdaptor<ResultSet, Map> {
	@Override
	public SqlOperation matchOperation() {
		return SqlOperation.SELECT;
	}

	@Override
	public Map adapt(ResultSet resultSet) {
		List<JsonObject> dataList = resultSet.getRows();
		if (dataList.size() == 0) {
			return null;
		} else {
			return dataList.get(0).getMap();
		}
	}
}
