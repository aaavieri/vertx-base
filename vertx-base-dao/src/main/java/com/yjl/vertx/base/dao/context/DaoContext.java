package com.yjl.vertx.base.dao.context;

import com.yjl.vertx.base.dao.adaptor.AbstractDaoAdaptor;
import com.yjl.vertx.base.dao.command.SqlCommandBuilder;
import com.yjl.vertx.base.dao.executor.AbstractSqlCommandExecutor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

@Data
@Accessors(fluent = true)
public class DaoContext {

	private SqlCommandBuilder sqlCommandBuilder;

	private AbstractDaoAdaptor daoAdaptor;

	private AbstractSqlCommandExecutor sqlCommandExecutor;

	private Method method;
}
