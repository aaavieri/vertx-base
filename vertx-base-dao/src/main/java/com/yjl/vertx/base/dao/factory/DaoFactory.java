package com.yjl.vertx.base.dao.factory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.generator.ProxyGeneratorIf;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.dao.anno.component.Dao;

import java.util.stream.Stream;

@ComponentInitializer(factoryClass = MysqlSqlClientFactory.class)
@ComponentInitializer(factoryClass = DaoAdaptorFactory.class, value = "com.yjl.vertx.base.dao.adaptor")
@ComponentInitializer(factoryClass = SqlExecutorFactory.class, value = "com.yjl.vertx.base.dao.executor")
@ComponentInitializer(factoryClass = DaoContextCacheFactory.class)
@ComponentInitializer(factoryClass = DefaultDaoGeneratorFactory.class)
public class DaoFactory extends BaseAnnotationComponentFactory {

	@Inject
    @Named("defaultDaoGenerator")
	private ProxyGeneratorIf defaultDaoGenerator;

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
			.forEach(clazz -> this.bind(clazz).toInstance(ReflectionsUtil.autoCast(defaultDaoGenerator.getProxyInstance(clazz))));
	}

//	private <T> T getProxyInstance(Class<T> daoIf) {
//		InvocationHandler invocationHandler = (proxy, method, args) -> {
//			DaoContext daoContext = this.daoContextCache.getDaoContext(method);
//			SqlCommand command = daoContext.sqlCommandBuilder().build(new ParamMapBuilder().buildMethodCall(method, args).getParamMap());
//
//			Future<Object> future = Future.future();
//			this.sqlClient.getConnection(as -> {
//				if (as.succeeded()) {
//				    try {
//                        Future<?> executeFuture = daoContext.sqlCommandExecutor().execute(as.result(), command);
//                        executeFuture.setHandler(executeResult -> {
//                            try {
//                                as.result().close();
//                                if (executeResult.succeeded()) {
//                                    Object adaptResult = daoContext.daoAdaptor().adapt(executeResult.result());
//                                    future.complete(adaptResult);
//                                } else {
//                                    future.fail(executeResult.cause());
//                                }
//                            } catch (Throwable throwable) {
//                                future.fail(throwable);
//                            }
//                        });
//                    } catch (Throwable throwable) {
//				        future.fail(throwable);
//                    }
//				} else {
//					future.fail(as.cause());
//				}
//			});
//			return future;
//		};
//		return ReflectionsUtil.autoCast(Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { daoIf },
//			invocationHandler));
//	}
}
