package com.yjl.vertx.base.com.factory.component;

import com.yjl.vertx.base.com.component.ComponentScanner;
import com.yjl.vertx.base.com.factory.config.ConfigFactory;
import com.yjl.vertx.base.com.util.JsonUtil;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;

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
