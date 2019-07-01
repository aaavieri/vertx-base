package com.yjl.vertx.base.com.verticle;

import com.google.inject.Injector;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.anno.initializer.FirstClassComponent;
import com.yjl.vertx.base.com.factory.component.BaseComponentFactory;
import com.yjl.vertx.base.com.factory.component.SimpleSlf4jLogbackFactory;
import com.yjl.vertx.base.com.factory.component.VertxResourceFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

@FirstClassComponent(@ComponentInitializer(factoryClass = SimpleSlf4jLogbackFactory.class))
public class InitVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		super.start();
		System.out.println(Thread.currentThread().getName());
		vertx.executeBlocking(future -> {
			ApplicationContext.getInstance().initContext(this);
			this.afterInit(ApplicationContext.getInstance().getContext());
			future.complete();
		}, Void -> startFuture.complete());
	}

	private Injector initInjector() {
//		BaseComponentFactory rootFactory = this.getRootFactory();
//		rootFactory.beforeConfigure();
//		Injector rootContext = Guice.createInjector(rootFactory);
//		rootFactory.afterConfigure();
//		List<BaseComponentFactory> factories = new ArrayList<>();
//		Injector context = Stream.of(this.getClass().getAnnotationsByType(ComponentInitializer.class))
//			.filter(annotation -> !annotation.factoryClass().equals(rootFactory.getClass()))
//			.collect(Collectors.groupingBy(ComponentInitializer::order)).entrySet().stream()
//			.filter(entry -> !entry.getValue().isEmpty())
//			.sorted(Comparator.comparingInt(entry -> entry.getKey().value()))
//			.map(entry -> {
//				Module initModule = new AbstractModule() {
//					@Override
//					protected void configure() {
//						entry.getValue().stream().map(annotation -> new PrivateModule() {
//							@Override
//							protected void configure() {
//								this.bind(annotation.factoryClass()).asEagerSingleton();
//								this.bind(ComponentInitializer.class).toInstance(annotation);
//								this.expose(annotation.factoryClass());
//							}
//						}).forEach(this::install);
//					}
//				};
//				return new HashMap.SimpleImmutableEntry<>(initModule, entry.getValue());
//			})
//			.reduce(rootContext,
//				(injector, entry) -> {
//					Injector initModuleInjector = injector.createChildInjector(entry.getKey());
//					return initModuleInjector.createChildInjector(entry.getValue().stream().map(annotation ->
//						initModuleInjector.getInstance(annotation.factoryClass())).peek(BaseComponentFactory::beforeConfigure)
//						.peek(factories::add)
//						.collect(Collectors.toList()));
//				},
//				(injector1, injector2) -> injector1);
//		ApplicationContext.setContext(context);
//		factories.forEach(BaseComponentFactory::afterConfigure);
//		List<BaseComponentFactory> otherFactories = this.initOtherFactory();
//		if (!otherFactories.isEmpty()) {
//			otherFactories.forEach(BaseComponentFactory::beforeConfigure);
//			context = context.createChildInjector(otherFactories);
//			ApplicationContext.setContext(context);
//			otherFactories.forEach(BaseComponentFactory::afterConfigure);
//		}

//		List<List<BaseComponentFactory>> factories = Stream.concat(Stream.of(this.getClass().getAnnotationsByType(ComponentInitializer.class))
//			.collect(Collectors.groupingBy(ComponentInitializer::order)).entrySet()
//				.stream().map(entry -> {
//				List<BaseComponentFactory> factoryList = entry.getValue().stream().map(annotation -> {
//					try {
//						Class<? extends BaseComponentFactory> factoryClass = annotation.factoryClass();
//						BaseComponentFactory factory = factoryClass.newInstance();
//						if (factory instanceof BaseAnnotationComponentFactory) {
//							((BaseAnnotationComponentFactory) factory).toBuilder().vertx(this.vertx).config(this.config())
//									.packages(annotation.value()).singleton(annotation.singleton()).build();
//						}
//						return factory;
//					} catch (Throwable throwable) {
//						throwable.printStackTrace();
//					}
//					return null;
//				}).filter(Objects::nonNull).collect(Collectors.toList());
//				return new HashMap.SimpleImmutableEntry<>(entry.getKey(), factoryList);
//			}).sorted(Comparator.comparingInt(entry -> entry.getKey().value()))
//				.map(HashMap.SimpleImmutableEntry::getValue), Stream.of(this.initOtherFactory()))
//				.filter(list -> !list.isEmpty())
//				.peek(list -> list.forEach(BaseComponentFactory::beforeStart)).collect(Collectors.toList());
//		Injector injector = Guice.createInjector(factories.isEmpty() ? new ArrayList<>(): factories.get(0));
//		for (int i = 1; i < factories.size(); i++) {
//			injector = injector.createChildInjector(factories.get(i));
//		}
//		return context;
		return null;
	}

	protected void afterInit(Injector context) {

	}

	public BaseComponentFactory getRootFactory() {
		return new VertxResourceFactory().config(this.config()).vertx(vertx);
	}
}
