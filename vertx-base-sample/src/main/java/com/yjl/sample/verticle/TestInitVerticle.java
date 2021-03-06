package com.yjl.sample.verticle;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.yjl.sample.component.TestService;
import com.yjl.vertx.base.auth.factory.UsiAuthenticatorFactory;
import com.yjl.vertx.base.auth.factory.UsiAuthorizerFactory;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.anno.initializer.OverrideDependency;
import com.yjl.vertx.base.com.verticle.InitVerticle;
import com.yjl.vertx.base.dao.factory.DaoFactory;
import com.yjl.vertx.base.web.factory.component.RestHandlerV2Factory;
import com.yjl.vertx.base.web.factory.component.RestRouteV2Factory;
import com.yjl.vertx.base.webclient.factory.WebClientFactory;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;

import java.lang.annotation.Annotation;
import java.util.Map;

//@ComponentInitializer(factoryClass = AutoRouteDaoFactory.class, value = "com.yjl.vertx.base.test.dbmapper")
//@ComponentInitializer(factoryClass = RestRouteV2Factory.class)
//@OverrideDependency(value = @ComponentInitializer(factoryClass = RestHandlerV2Factory.class, value = "com.yjl.vertx.base.test.handler2"),
//	customInclude = {@ComponentInitializer(factoryClass = DaoFactory.class, value = "com.yjl.vertx.base.test.dbmapper"),
//		@ComponentInitializer(factoryClass = WebClientFactory.class, value = "com.yjl.vertx.base.test.client")})
////@ComponentInitializer(factoryClass = RestHandlerV2Factory.class, value = {"com.yjl.vertx.base.test.handler2"})
//@ComponentInitializer("com.yjl.vertx.base.test.component")
//@ComponentInitializer(factoryClass = SimpleSlf4jLogbackFactory.class)
//@ComponentInitializer(factoryClass = MysqlSqlClientFactory.class)
@OverrideDependency(value = @ComponentInitializer(factoryClass = RestRouteV2Factory.class),
    customInclude = {@ComponentInitializer(factoryClass = RestHandlerV2Factory.class, value = "com.yjl.sample.handler"),
        @ComponentInitializer(factoryClass = DaoFactory.class, value = "com.yjl.sample.mapper"),
        @ComponentInitializer(factoryClass = WebClientFactory.class, value = "com.yjl.sample.client")
    })
@ComponentInitializer(factoryClass = UsiAuthenticatorFactory.class)
@ComponentInitializer(factoryClass = UsiAuthorizerFactory.class)
public class TestInitVerticle extends InitVerticle {

	protected void afterInit(Injector context) {
//        JWTAuth provider = JWTAuth.create(vertx, new JWTAuthOptions().setJWTOptions(new JWTOptions()
//            .setExpiresInSeconds(3).setNoTimestamp(true))
//            );
//        String token = provider.generateToken(new JsonObject().put("userInfo", "aaavieri"));
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        provider.authenticate(new JsonObject().put("jwt", token), res -> {
//            if (res.succeeded()) {
//                User theUser = res.result();
//                System.out.println(theUser.principal());
//            } else {
//                // Failed!
//                res.cause().printStackTrace();
//            }
//        });
        
    }

	public static void main(String[] args) throws NoSuchFieldException {
        System.out.println(Integer.MAX_VALUE + 10);
//		ComponentInitializer initializer1 = Stream.of(AutoRouteDaoFactory.class.getAnnotationsByType(ComponentInitializer.class))
////			.filter(initializer -> initializer.factoryClass().equals(DaoAdaptorFactory.class))
////			.findFirst().get();
////		ComponentInitializer initializer2 = Stream.of(DaoFactory.class.getAnnotationsByType(ComponentInitializer.class))
////			.filter(initializer -> initializer.factoryClass().equals(DaoAdaptorFactory.class))
////			.findFirst().get();
////		System.out.println(new FactoryFamilyNode().realNode(initializer1).equals(new FactoryFamilyNode().realNode(initializer2)));
	}

	static class Test {
		private String s;
		public Test setS(String s) {
			this.s = s;
			return this;
		}

		public String getS() {
			return this.s;
		}
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
