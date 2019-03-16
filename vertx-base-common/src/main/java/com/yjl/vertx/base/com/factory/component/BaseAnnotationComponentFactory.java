package com.yjl.vertx.base.com.factory.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;

public abstract class BaseAnnotationComponentFactory extends BaseComponentFactory {

	@Inject(optional = true)
	protected ComponentInitializer metaData;
}
