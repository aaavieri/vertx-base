package com.yjl.vertx.base.dao.factory.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.dao.anno.component.Dao;
import com.yjl.vertx.base.dao.factory.dao.InvocationHandlerFactory;
import io.vertx.ext.sql.SQLClient;

import java.lang.reflect.Proxy;
import java.util.stream.Stream;

public class DaoFactory extends BaseAnnotationComponentFactory {

	@Inject(optional = true)
	private SQLClient sqlClient;

	@Override
	public void configure() {
		Stream.of(this.metaData.value()).flatMap(packageName -> ReflectionsUtil.getClassesByAnnotation(packageName, Dao.class).stream())
			.filter(clazz -> {
				if (!clazz.isInterface()) {
					this.getLogger().warn("warning: {} is not interface, skipped", clazz.getName());
					return false;
				}
				return true;
			})
			.forEach(clazz -> this.bind(clazz).toInstance(ReflectionsUtil.autoCast(this.getProxyInstance(clazz))));
	}

	private <T> T getProxyInstance(Class<T> daoIf) {
		return ReflectionsUtil.autoCast(Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{daoIf},
			InvocationHandlerFactory.getInvocationHandler(this.sqlClient)));
	}
}
