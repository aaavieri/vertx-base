package com.yjl.vertx.base.dao.factory.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.Order;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.util.JsonUtil;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.com.util.StringUtil;
import com.yjl.vertx.base.dao.adaptor.AbstractAdaptor;
import com.yjl.vertx.base.dao.anno.component.AutoRouteDao;
import com.yjl.vertx.base.dao.anno.component.AutoRouteDaoMethod;
import com.yjl.vertx.base.dao.anno.component.Dao;
import com.yjl.vertx.base.dao.command.SqlCommand;
import com.yjl.vertx.base.dao.command.SqlCommandBuilder;
import com.yjl.vertx.base.dao.command.SqlCommandParamMapBuilder;
import com.yjl.vertx.base.dao.executor.AbstractCommandExecutor;
import com.yjl.vertx.base.web.enumeration.RouteMethod;
import com.yjl.vertx.base.web.factory.component.BaseRestRouteFactory;
import com.yjl.vertx.base.web.factory.component.DefaultFailureHandlerFactory;
import com.yjl.vertx.base.web.handler.HandlerWrapper;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ComponentInitializer(factoryClass = MysqlSqlClientFactory.class)
@ComponentInitializer(factoryClass = AdaptorFactory.class, value = "com.yjl.vertx.base.dao.adaptor")
@ComponentInitializer(factoryClass = SqlExecutorFactory.class, value = "com.yjl.vertx.base.dao.executor")
@ComponentInitializer(factoryClass = DefaultFailureHandlerFactory.class)
public class AutoRouteDaoFactory extends BaseRestRouteFactory {

	@Inject
	private SQLClient sqlClient;

	@Inject
	private Set<AbstractAdaptor> adaptors;

	@Inject
	private Set<AbstractCommandExecutor> commandExecutors;

	private Map<Method, AbstractAdaptor> adaptorMap = new HashMap<>();

	private Map<Method, AbstractCommandExecutor> executorMap = new HashMap<>();

	private Map<Method, SqlCommandBuilder> builderMap = new HashMap<>();

	@Override
	protected List<HandlerWrapper> getHandlerWrapperList() {
		return Stream.concat(Stream.of(this.metaData.value()).flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName, Dao.class).stream()),
			Stream.of(this.metaData.value()).flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName, AutoRouteDao.class).stream()))
			.filter(clazz -> {
				if (!clazz.isInterface()) {
					this.getLogger().warn("warning: {} is not interface, skipped", clazz.getName());
					return false;
				}
				return true;
			})
			.flatMap(clazz -> Stream.of(clazz.getMethods())
				.map(method -> {
					AutoRouteDao autoRouteDao = clazz.getAnnotation(AutoRouteDao.class);
					AutoRouteDaoMethod autoRouteDaoMethod = method.getAnnotation(AutoRouteDaoMethod.class);
					Order order = method.getAnnotation(Order.class);
					String parentUrl = autoRouteDao == null || StringUtil.isBlank(autoRouteDao.value())
						? clazz.getSimpleName() : autoRouteDao.value();
					String childUrl = autoRouteDaoMethod == null || StringUtil.isBlank(autoRouteDaoMethod.value())
						? method.getName() : autoRouteDaoMethod.value();
					Handler<RoutingContext> handler = context -> {
						JsonObject jsonObject = new JsonObject();
						Consumer<Map.Entry<String, ?>> entryConsumer = entry -> jsonObject.put(entry.getKey(), entry.getValue());
						context.request().params().forEach(entryConsumer);
						context.request().formAttributes().forEach(entryConsumer);
						String body = context.getBodyAsString();
						if (JsonUtil.isJson(body)) {
							context.getBodyAsJson().forEach(entryConsumer);
						}
						if (!this.builderMap.containsKey(method)) {
							this.builderMap.put(method, SqlCommandBuilder.newInstance(method));
						}
						SqlCommand command = this.builderMap.get(method).build(new SqlCommandParamMapBuilder().buildJsonObject(jsonObject).getParamMap());
						if (!this.executorMap.containsKey(method)) {
							AbstractCommandExecutor executor = this.commandExecutors.stream().filter(commandExecutor -> commandExecutor.isMatch(command))
								.findFirst().orElseThrow(() -> new FrameworkException().message("can not find executor for:" + method.getName()));
							this.executorMap.put(method, executor);
						}
						if (!this.adaptorMap.containsKey(method)) {
							AbstractAdaptor executor = this.adaptors.stream().filter(adaptor -> adaptor.isMatch(command))
								.findFirst().orElseThrow(() -> new FrameworkException().message("can not find adaptor for:" + method.getName()));
							this.adaptorMap.put(method, executor);
						}
						this.sqlClient.getConnection(as -> {
							if (as.succeeded()) {
								Future<?> executeFuture = this.executorMap.get(method).execute(as.result(), command);
								executeFuture.setHandler(executeResult -> {
									as.result().close();
									if (executeResult.succeeded()) {
										Object adaptResult = this.adaptorMap.get(method).adapt(executeResult.result());
										context.response().end(new JsonObject().put("data", adaptResult).toBuffer());
									} else {
										context.fail(executeResult.cause());
									}
								});
							} else {
								context.fail(as.cause());
							}
						});
					};
					return new HandlerWrapper().order(order == null ? Integer.MAX_VALUE : order.value())
						.handler(handler).url(StringUtil.concatPath(parentUrl, childUrl))
						.method(autoRouteDaoMethod != null ? autoRouteDaoMethod.route() : RouteMethod.GET)
						.regexp(false).autoHandleError(true);
				})
			).collect(Collectors.toList());
	}
}
