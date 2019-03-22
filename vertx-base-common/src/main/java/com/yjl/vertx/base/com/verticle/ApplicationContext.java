package com.yjl.vertx.base.com.verticle;

import com.google.inject.Injector;
import com.yjl.vertx.base.com.factory.component.BaseComponentFactory;
import com.yjl.vertx.base.com.factory.family.FactoryFamily;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationContext {

	private static ApplicationContext instance;

	private FactoryFamily factoryFamily;

	public static ApplicationContext getInstance() {
		if (instance == null) {
			instance = new ApplicationContext();
		}
		return instance;
	}

	public void initContext(InitVerticle verticle) {
		try {
			this.factoryFamily = new FactoryFamily().initFamilyTree(verticle).initFamily();
			this.factoryFamily.start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public Injector getContext() {
		return this.factoryFamily.getInjector();
	}

	public BaseComponentFactory getRootFactory() {
		return this.factoryFamily.getRootNode().nodeInstance();
	}
}
