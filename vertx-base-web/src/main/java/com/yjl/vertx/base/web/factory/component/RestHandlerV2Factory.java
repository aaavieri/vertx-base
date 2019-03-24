package com.yjl.vertx.base.web.factory.component;

import com.google.inject.multibindings.Multibinder;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.web.anno.component.RestRouteV2Handler;
import com.yjl.vertx.base.web.anno.handler.RestV2HandlerInject;
import com.yjl.vertx.base.web.handler.BaseRouteV2Handler;

import java.util.stream.Stream;

@ComponentInitializer(factoryClass = DefaultFailureHandlerFactory.class)
public class RestHandlerV2Factory extends BaseAnnotationComponentFactory {

	@Override
	public void configure() {
		Multibinder<BaseRouteV2Handler> handlerBinder = Multibinder.newSetBinder(binder(), BaseRouteV2Handler.class, RestV2HandlerInject.class);
		Stream.of(this.metaData.value())
			.flatMap(packageName -> ReflectionsUtil.getClasses(packageName, BaseRouteV2Handler.class, RestRouteV2Handler.class).stream())
			.peek(clazz -> this.bind(clazz).asEagerSingleton())
			.forEach(clazz -> handlerBinder.addBinding().to(clazz));
	}
}
