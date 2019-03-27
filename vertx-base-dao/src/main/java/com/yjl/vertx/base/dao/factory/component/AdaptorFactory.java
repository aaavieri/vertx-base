package com.yjl.vertx.base.dao.factory.component;

import com.google.inject.multibindings.Multibinder;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.dao.adaptor.AbstractAdaptor;
import com.yjl.vertx.base.dao.enumeration.SqlOperation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdaptorFactory extends BaseAnnotationComponentFactory {

//	private static List<AbstractAdaptor> adaptorList;
//
//	public static AbstractAdaptor getAdaptor(SqlOperation sqlOperation, Type returnType) {
//		if (adaptorList == null) {
//			adaptorList = ReflectionsUtil.getClassesByBaseClass("com.yjl.vertx.base.dao.adaptor", AbstractAdaptor.class).stream()
//				.map(clazz -> {
//					try {
//						return clazz.newInstance();
//					} catch (Throwable t) {
//						throw new FrameworkException().message("init failure: " + clazz.getName());
//					}
//				})
//				.collect(Collectors.toList());
//		}
//		return adaptorList.stream().filter(adaptor -> adaptor.matchOperation().equals(sqlOperation))
//			.filter(adaptor -> {
//				Type adaptType = ReflectionsUtil.<ParameterizedType>autoCast(adaptor.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
//				return adaptType.equals(returnType);
//			}).findFirst().orElseThrow(() -> new FrameworkException().message("can not find adaptor for: " + returnType.getTypeName()));
//	}

	@Override
	public void configure() {
		Multibinder<AbstractAdaptor> handlerBinder = Multibinder.newSetBinder(this.binder(), AbstractAdaptor.class);
		Stream.of(this.metaData.value())
			.flatMap(packageName -> ReflectionsUtil.getClassesByBaseClass(packageName, AbstractAdaptor.class).stream())
			.peek(clazz -> this.bind(clazz).asEagerSingleton())
			.forEach(clazz -> handlerBinder.addBinding().to(clazz));
	}
}
