package com.yjl.vertx.base.dao.command;

import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.json.JsonArray;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Type;

@Accessors(fluent = true)
@Data
public class SqlCommand {

	private String sql;

	private boolean withParams;

	private JsonArray params;

	private SqlOperation sqlOperation;

	private Type returnType;
}
