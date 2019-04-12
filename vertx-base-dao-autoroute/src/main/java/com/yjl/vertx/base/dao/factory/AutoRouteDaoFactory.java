package com.yjl.vertx.base.dao.factory;

import com.google.inject.Inject;
import com.yjl.vertx.base.autoroute.anno.AutoRouteIf;
import com.yjl.vertx.base.autoroute.util.AutoRouteUtil;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.builder.ParamMapBuilder;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.dao.anno.component.Dao;
import com.yjl.vertx.base.dao.command.SqlCommand;
import com.yjl.vertx.base.dao.context.DaoContext;
import com.yjl.vertx.base.dao.context.DaoContextCache;
import com.yjl.vertx.base.web.factory.component.BaseRestRouteFactory;
import com.yjl.vertx.base.web.factory.component.DefaultFailureHandlerFactory;
import com.yjl.vertx.base.web.factory.component.HttpServerFactory;
import com.yjl.vertx.base.web.handler.HandlerWrapper;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ComponentInitializer(factoryClass = MysqlSqlClientFactory.class)
@ComponentInitializer(factoryClass = DaoAdaptorFactory.class, value = "com.yjl.vertx.base.dao.adaptor")
@ComponentInitializer(factoryClass = SqlExecutorFactory.class, value = "com.yjl.vertx.base.dao.executor")
@ComponentInitializer(factoryClass = DefaultFailureHandlerFactory.class)
@ComponentInitializer(factoryClass = HttpServerFactory.class)
@ComponentInitializer(factoryClass = DaoContextCacheFactory.class)
public class AutoRouteDaoFactory extends BaseRestRouteFactory {

	@Inject
	private SQLClient sqlClient;

	@Inject
	private DaoContextCache daoContextCache;

	@Override
	protected List<HandlerWrapper> getHandlerWrapperList() {
		return Stream.concat(Stream.of(this.metaData.value()).flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName, Dao.class).stream()),
			Stream.of(this.metaData.value()).flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName, AutoRouteIf.class).stream()))
			.filter(clazz -> {
				if (!clazz.isInterface()) {
					this.getLogger().warn("warning: {} is not interface, skipped", clazz.getName());
					return false;
				}
				return true;
			})
			.flatMap(clazz -> Stream.of(clazz.getMethods())
				.map(method -> {
					Handler<RoutingContext> handler = context -> {
						JsonObject jsonObject = AutoRouteUtil.getRequestParam(context);
						DaoContext daoContext = this.daoContextCache.getDaoContext(method);
						SqlCommand command = daoContext.sqlCommandBuilder().build(new ParamMapBuilder().buildJsonObject(jsonObject).getParamMap());
						this.sqlClient.getConnection(as -> {
							if (as.succeeded()) {
								Future<?> executeFuture = daoContext.sqlCommandExecutor().execute(as.result(), command);
								executeFuture.setHandler(executeResult -> {
									as.result().close();
									if (executeResult.succeeded()) {
										Object adaptResult = daoContext.daoAdaptor().adapt(executeResult.result());
										context.response().end(Json.encodeToBuffer(adaptResult));
									} else {
										context.fail(executeResult.cause());
									}
								});
							} else {
								context.fail(as.cause());
							}
						});
					};
					return AutoRouteUtil.getHandlerWrapper(method, handler);
				})
			).collect(Collectors.toList());
	}
}
