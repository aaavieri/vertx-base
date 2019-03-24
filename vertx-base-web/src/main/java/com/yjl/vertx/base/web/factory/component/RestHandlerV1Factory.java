package com.yjl.vertx.base.web.factory.component;

import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.yjl.vertx.base.com.anno.Order;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.util.OrderUtil;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.com.util.StringUtil;
import com.yjl.vertx.base.com.verticle.ApplicationContext;
import com.yjl.vertx.base.web.anno.component.RestRouteV1Handler;
import com.yjl.vertx.base.web.anno.component.RestRouteMapping;
import com.yjl.vertx.base.web.anno.handler.impl.RestV1HandlerInjectImpl;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestHandlerV1Factory extends BaseAnnotationComponentFactory {

	@Override
	public void configure() {
		Multibinder<Object> handlerBinder = Multibinder.newSetBinder(binder(), Object.class, new RestV1HandlerInjectImpl(""));
		Stream.of(this.metaData.value())
			.flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName, RestRouteV1Handler.class).stream())
			.peek(clazz -> this.bind(clazz).asEagerSingleton()).forEach(clazz -> handlerBinder.addBinding().to(clazz));
	}
}