package com.yjl.vertx.base.dao.factory;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.builder.ParamMapBuilder;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.dao.anno.component.Dao;
import com.yjl.vertx.base.dao.command.SqlCommand;
import com.yjl.vertx.base.dao.context.DaoContext;
import com.yjl.vertx.base.dao.context.DaoContextCache;
import io.vertx.core.Future;
import io.vertx.ext.sql.SQLClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.stream.Stream;

@ComponentInitializer(factoryClass = MysqlSqlClientFactory.class)
@ComponentInitializer(factoryClass = DaoAdaptorFactory.class, value = "com.yjl.vertx.base.dao.adaptor")
@ComponentInitializer(factoryClass = SqlExecutorFactory.class, value = "com.yjl.vertx.base.dao.executor")
@ComponentInitializer(factoryClass = DaoContextCacheFactory.class)
public class DaoFactory extends BaseAnnotationComponentFactory {

	@Inject
	private SQLClient sqlClient;

	@Inject
	private DaoContextCache daoContextCache;

	@Override
	public void configure() {
		Stream.of(this.metaData.value()).flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName, Dao.class).stream())
			.filter(clazz -> {
				if (!clazz.isInterface()) {
					this.getLogger().warn("warning: {} is not interface, skipped", clazz.getName());
					return false;
				}
				return true;
			})
			.forEach(clazz -> this.bind(clazz).toInstance(ReflectionsUtil.autoCast(this.getProxyInstance(clazz))));
	}

	private <T> T getProxyInstance(Class<T> daoIf) {
		InvocationHandler invocationHandler = (proxy, method, args) -> {
			DaoContext daoContext = this.daoContextCache.getDaoContext(method);
			SqlCommand command = daoContext.sqlCommandBuilder().build(new ParamMapBuilder().buildMethodCall(method, args).getParamMap());

			Future<Object> future = Future.future();
			this.sqlClient.getConnection(as -> {
				if (as.succeeded()) {
					Future<?> executeFuture = daoContext.sqlCommandExecutor().execute(as.result(), command);
					executeFuture.setHandler(executeResult -> {
						as.result().close();
						if (executeResult.succeeded()) {
							Object adaptResult = daoContext.daoAdaptor().adapt(executeResult.result());
							future.complete(adaptResult);
						} else {
							future.fail(executeResult.cause());
						}
					});
				} else {
					future.fail(as.cause());
				}
			});
			return future;
		};
		return ReflectionsUtil.autoCast(Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { daoIf },
			invocationHandler));
	}
}
