package com.yjl.vertx.base.dao.factory.component;

import com.google.inject.multibindings.Multibinder;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.dao.command.SqlCommand;
import com.yjl.vertx.base.dao.executor.AbstractCommandExecutor;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlExecutorFactory extends BaseAnnotationComponentFactory {

//	private static List<AbstractCommandExecutor> executorList;
//
//	public static AbstractCommandExecutor getExecutor(SqlCommand command) {
//		if (executorList == null) {
//			Set<Class<? extends AbstractCommandExecutor>> executorFactories = ReflectionsUtil.getClassesByBaseClass("com.yjl.vertx.base.dao.executor", AbstractCommandExecutor.class);
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

	@Override
	public void configure() {
		Multibinder<AbstractCommandExecutor> handlerBinder = Multibinder.newSetBinder(this.binder(), AbstractCommandExecutor.class);
		Stream.of(this.metaData.value())
			.flatMap(packageName -> ReflectionsUtil.getClassesByBaseClass(packageName, AbstractCommandExecutor.class).stream())
			.peek(clazz -> this.bind(clazz).asEagerSingleton())
			.forEach(clazz -> handlerBinder.addBinding().to(clazz));
	}
}
