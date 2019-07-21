package com.yjl.vertx.base.com.factory.component;

import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.yjl.vertx.base.com.component.ComponentScanner;
import com.yjl.vertx.base.com.factory.config.ConfigFactory;
import com.yjl.vertx.base.com.util.JsonUtil;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Accessors(fluent = true)
@Data
public class VertxResourceFactory extends BaseComponentFactory {

	private Vertx vertx;

	private JsonObject config;

	@Override
	public void configure() {
		this.bind(Vertx.class).toInstance(this.vertx);
		this.bind(JsonObject.class).annotatedWith(ConfigFactory.getConfig("")).toInstance(this.config);
		this.bind(JsonObject.class).annotatedWith(ConfigFactory.getConfig(".")).toInstance(this.config);
		this.bind(ComponentScanner.class).asEagerSingleton();
		JsonObject extendJsonObject = JsonUtil.extendJsonObject(this.config);
        this.bind(JsonObject.class).annotatedWith(ConfigFactory.getConfig(".extend")).toInstance(extendJsonObject);
        extendJsonObject.forEach(entry -> this.bind(entry.getValue().getClass())
            .annotatedWith(ConfigFactory.getConfig(entry.getKey()))
            .toInstance(ReflectionsUtil.autoCast(entry.getValue())));
        this.bind(TypeLiteral.get(new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] {String.class, String.class};
            }
            @Override
            public Type getRawType() {
                return Map.class;
            }
            @Override
            public Type getOwnerType() {
                return null;
            }
        })).annotatedWith(Names.named("env")).toInstance(ReflectionsUtil.autoCast(new HashMap<>(System.getenv())));
//		this.bindConfig("", this.config);
	}

//	private void bindConfig(String parentKey, JsonObject config) {
//		config.fieldNames().forEach(fieldName -> {
//			Object value = config.getValue(fieldName);
//			String absoluteKey = parentKey + fieldName;
//			this.bind(value.getClass())
//					.annotatedWith(ConfigFactory.getConfig(absoluteKey))
//					.toInstance(ReflectionsUtil.autoCast(value));
//			if (value instanceof JsonObject) {
//				this.bindConfig(absoluteKey + ".", ReflectionsUtil.autoCast(value));
//			}
//		});
//	}
}
