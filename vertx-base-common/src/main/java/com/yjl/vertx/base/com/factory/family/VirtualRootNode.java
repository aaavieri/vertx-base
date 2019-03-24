package com.yjl.vertx.base.com.factory.family;

import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.factory.component.BaseComponentFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;


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

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ComponentInitializer)) {
			return false;
		} else {
			ComponentInitializer other = (ComponentInitializer)obj;
			return this.rootFactoryClass.equals(other.factoryClass())
				&& Arrays.equals(this.value(), other.value())
				&& this.singleton() == other.singleton();
		}
	}

	@Override
	public int hashCode() {
		return 127 * "factoryClass".hashCode() ^ this.factoryClass().hashCode()
			+ 127 * "singleton".hashCode() ^ Boolean.valueOf(this.singleton()).hashCode()
			+ 127 * "value".hashCode() ^ Arrays.hashCode(this.value());
	}
}
