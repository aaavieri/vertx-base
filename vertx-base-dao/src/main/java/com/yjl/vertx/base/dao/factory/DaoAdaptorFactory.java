package com.yjl.vertx.base.dao.factory;

import com.yjl.vertx.base.com.factory.component.SimpleSetFactory;
import com.yjl.vertx.base.dao.adaptor.AbstractDaoAdaptor;

public class DaoAdaptorFactory extends SimpleSetFactory<AbstractDaoAdaptor> {

//	@Override
//	public void configure() {
//		Multibinder<AbstractDaoAdaptor> handlerBinder = Multibinder.newSetBinder(this.binder(), AbstractDaoAdaptor.class);
//		Stream.of(this.metaData.value())
//			.flatMap(packageName -> ReflectionsUtil.getClassesByBaseClass(packageName, AbstractDaoAdaptor.class).stream())
//			.peek(clazz -> this.bind(clazz).asEagerSingleton())
//			.forEach(clazz -> handlerBinder.addBinding().to(clazz));
//	}

	@Override
	protected Class<AbstractDaoAdaptor> getAbstractParentClass() {
		return AbstractDaoAdaptor.class;
	}
}
