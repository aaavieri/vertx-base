package com.yjl.vertx.base.web.factory.component;

import com.google.inject.multibindings.Multibinder;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.web.anno.component.RestRouteV1Handler;
import com.yjl.vertx.base.web.anno.handler.RestV1HandlerInject;

import java.util.stream.Stream;

@ComponentInitializer(factoryClass = DefaultFailureHandlerFactory.class)
public class RestHandlerV1Factory extends BaseAnnotationComponentFactory {

	@Override
	public void configure() {
		Multibinder<Object> handlerBinder = Multibinder.newSetBinder(binder(), Object.class, RestV1HandlerInject.class);
		Stream.of(this.metaData.value())
			.flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName, RestRouteV1Handler.class).stream())
			.peek(clazz -> this.bind(clazz).asEagerSingleton())
			.forEach(clazz -> handlerBinder.addBinding().to(clazz));
	}
}