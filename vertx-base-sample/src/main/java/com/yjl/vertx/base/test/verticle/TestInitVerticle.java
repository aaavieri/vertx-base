package com.yjl.vertx.base.test.verticle;

import com.google.inject.*;
import com.yjl.vertx.base.com.anno.Order;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.factory.component.SimpleSlf4jLogbackFactory;
import com.yjl.vertx.base.com.verticle.InitVerticle;
import com.yjl.vertx.base.dao.factory.component.DaoFactory;
import com.yjl.vertx.base.dao.factory.component.MysqlSqlClientFactory;
import com.yjl.vertx.base.test.component.Test2Service;
import com.yjl.vertx.base.test.component.TestService;
import com.yjl.vertx.base.web.factory.component.RestRouteV2Factory;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Stream;

@ComponentInitializer(factoryClass = DaoFactory.class, value = "com.yjl.vertx.base.test.dbmapper")
@ComponentInitializer(factoryClass = RestRouteV2Factory.class, value = {"com.yjl.vertx.base.test.handler2"})
@ComponentInitializer("com.yjl.vertx.base.test.component")
@ComponentInitializer(factoryClass = SimpleSlf4jLogbackFactory.class, order = @Order(0))
@ComponentInitializer(factoryClass = MysqlSqlClientFactory.class, order = @Order(0))
public class TestInitVerticle extends InitVerticle {

	protected void afterInit(Injector context) {
		context.getInstance(Test2Service.class).test();
	}

	public static void main(String[] args) throws NoSuchFieldException {
		Stream.of(TestInitVerticle.class.getMethods()).filter(method -> method.getName().equals("test"))
			.findFirst().ifPresent(method -> {
				System.out.println(method.getGenericReturnType());
				Type argType = ((ParameterizedType)method.getGenericReturnType()).getActualTypeArguments()[0];
				System.out.println(((Class)argType).getName());
				System.out.println(argType.getClass());
			});
		Injector injector = Guice.createInjector().createChildInjector(new AbstractModule() {
			@Override
			protected void configure() {
				this.install(new PrivateModule() {
					@Override
					protected void configure() {
						this.bind(F1.class).asEagerSingleton();
						this.bind(Integer.class).toInstance(9);
						this.bind(JsonObject.class).annotatedWith(new ConfigImpl("test")).toInstance(new JsonObject().put("key", "test"));
						this.expose(F1.class);
					}
				});
				this.install(new PrivateModule() {
					@Override
					protected void configure() {
						this.bind(F2.class).asEagerSingleton();
						this.bind(Integer.class).toInstance(10);
						this.expose(F2.class);
					}
				});
			}
		});
		injector = injector.createChildInjector(injector.getInstance(F1.class))
				.createChildInjector(injector.getInstance(F2.class));
		System.out.println(injector.getProvider(HttpServer.class).get());
		System.out.println(injector.getProvider(TestService.class).get());
	}

	public Future<Map<String, Object>> test() {
		return null;
	}

	static class ConfigImpl implements Config {

		private String value;

		ConfigImpl(String value) {
			this.value = value;
		}

		@Override
		public String value() {
			return this.value;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return Config.class;
		}
		public int hashCode() {
			return 127 * "value".hashCode() ^ this.value.hashCode();
		}

		public boolean equals(Object o) {
			if (!(o instanceof Config)) {
				return false;
			} else {
				Config other = (Config)o;
				return this.value.equals(other.value());
			}
		}
	}

	static class F1 extends AbstractModule {

		@Config("test")
		@Inject
		private JsonObject test;

		@Override
		protected void configure() {
			System.out.println(this.test);
			this.bind(HttpServer.class).toInstance(Vertx.vertx().createHttpServer());
		}
	}

	static class F2 extends AbstractModule {

		@Inject
		private Integer hello = 11;

		@Inject(optional = true)
		private HttpServer httpServer;

		@Override
		protected void configure() {
			System.out.println(this.hello);
			System.out.println(this.httpServer);
			this.bind(TestService.class);
		}
	}
}
