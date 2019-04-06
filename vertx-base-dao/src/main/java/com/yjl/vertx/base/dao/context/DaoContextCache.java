package com.yjl.vertx.base.dao.context;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.dao.adaptor.AbstractDaoAdaptor;
import com.yjl.vertx.base.dao.command.SqlCommand;
import com.yjl.vertx.base.dao.command.SqlCommandBuilder;
import com.yjl.vertx.base.dao.executor.AbstractSqlCommandExecutor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DaoContextCache {

	@Inject
	private Set<AbstractDaoAdaptor> adaptors;

	@Inject
	private Set<AbstractSqlCommandExecutor> commandExecutors;

	private List<DaoContext> contextList = new ArrayList<>();

	public DaoContext getDaoContext(Method method) {
		return this.contextList.stream().filter(daoContext -> daoContext.method().equals(method))
			.findFirst().orElseGet(() -> {
				SqlCommandBuilder builder = SqlCommandBuilder.newInstance(method);
				AbstractSqlCommandExecutor commandExecutor = this.commandExecutors.stream().filter(executor -> executor.isMatch(builder.sqlOperation()))
					.findFirst().orElseThrow(() -> new FrameworkException().message("can not find executor for:" + method.getName()));
				AbstractDaoAdaptor daoAdaptor = this.adaptors.stream().filter(adaptor -> adaptor.isMatch(builder.realReturnType()))
					.findFirst().orElseThrow(() -> new FrameworkException().message("can not find adaptor for:" + method.getName()));
				DaoContext daoContext = new DaoContext().daoAdaptor(daoAdaptor).method(method).sqlCommandBuilder(builder).sqlCommandExecutor(commandExecutor);
				this.contextList.add(daoContext);
				return daoContext;
			});
	}
}
