package com.yjl.vertx.base.com.factory.family;

import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.factory.component.BaseComponentFactory;
import lombok.EqualsAndHashCode;

import java.lang.annotation.Annotation;

@EqualsAndHashCode
public class VirtualRootNode implements ComponentInitializer {

	private Class<? extends BaseComponentFactory> rootFactoryClass;

	VirtualRootNode(Class<? extends BaseComponentFactory> rootFactoryClass) {
		this.rootFactoryClass = rootFactoryClass;
	}

	@Override
	public Class<? extends BaseComponentFactory> factoryClass() {
		return this.rootFactoryClass;
	}

	@Override
	public String[] value() {
		return new String[0];
	}

	@Override
	public boolean singleton() {
		return true;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return ComponentInitializer.class;
	}
}
