package com.yjl.vertx.base.com.factory.component;

import com.yjl.vertx.base.com.anno.component.Component;
import com.yjl.vertx.base.com.util.ReflectionsUtil;

import java.util.stream.Stream;

public class CommonComponentFactory extends BaseAnnotationComponentFactory {

	@Override
	public void configure() {
		Stream.of(this.metaData.value()).flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName,
				Component.class).stream()).forEach(clazz -> {
			if (this.metaData.singleton()) {
				this.bind(clazz).asEagerSingleton();
			} else {
				this.bind(clazz);
			}
		});
	}
}
