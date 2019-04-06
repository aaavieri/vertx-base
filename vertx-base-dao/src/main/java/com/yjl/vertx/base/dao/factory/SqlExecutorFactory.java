package com.yjl.vertx.base.dao.factory;

import com.yjl.vertx.base.com.factory.component.SimpleSetFactory;
import com.yjl.vertx.base.dao.executor.AbstractSqlCommandExecutor;

public class SqlExecutorFactory extends SimpleSetFactory<AbstractSqlCommandExecutor> {

//	private static List<AbstractSqlCommandExecutor> executorList;
//
//	public static AbstractSqlCommandExecutor getExecutor(SqlCommand command) {
//		if (executorList == null) {
//			Set<Class<? extends AbstractSqlCommandExecutor>> executorFactories = ReflectionsUtil.getClassesByBaseClass("com.yjl.vertx.base.dao.executor", AbstractSqlCommandExecutor.class);
//			executorList = executorFactories.stream().filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
//				.map(clazz -> {
//					try {
//						return clazz.newInstance();
//					} catch (Throwable t) {
//						throw new FrameworkException().message("init failure: " + clazz.getName());
//					}
//				})
//				.collect(Collectors.toList());
//
//		}
//		return executorList.stream().filter(executor -> command.sqlOperation().equals(executor.getSqlOperation()))
//			.findFirst().orElseThrow(() -> new FrameworkException().message("no match executor: " + command.sqlOperation().name()));
//	}

//	@Override
//	public void configure() {
//		Multibinder<AbstractSqlCommandExecutor> handlerBinder = Multibinder.newSetBinder(this.binder(), AbstractSqlCommandExecutor.class);
//		Stream.of(this.metaData.value())
//			.flatMap(packageName -> ReflectionsUtil.getClassesByBaseClass(packageName, AbstractSqlCommandExecutor.class).stream())
//			.peek(clazz -> this.bind(clazz).asEagerSingleton())
//			.forEach(clazz -> handlerBinder.addBinding().to(clazz));
//	}

	@Override
	protected Class<AbstractSqlCommandExecutor> getAbstractParentClass() {
		return AbstractSqlCommandExecutor.class;
	}
}
