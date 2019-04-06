package com.yjl.vertx.base.com.factory.component;

import com.google.inject.multibindings.Multibinder;
import com.yjl.vertx.base.com.util.ReflectionsUtil;

import java.util.stream.Stream;

public abstract class SimpleSetFactory<T> extends BaseAnnotationComponentFactory {
	@Override
	public void configure() {
		Class<T> abstractParentClass = this.getAbstractParentClass();
		Multibinder<T> handlerBinder = Multibinder.newSetBinder(this.binder(), abstractParentClass);
		Stream.of(this.metaData.value())
			.flatMap(packageName -> ReflectionsUtil.getClassesByBaseClass(packageName, abstractParentClass).stream())
			.peek(clazz -> this.bind(clazz).asEagerSingleton())
			.forEach(clazz -> handlerBinder.addBinding().to(clazz));
	}

	protected abstract Class<T> getAbstractParentClass();
}
