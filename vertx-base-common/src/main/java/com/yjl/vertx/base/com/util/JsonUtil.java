package com.yjl.vertx.base.com.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;
import java.util.function.BiConsumer;

public class JsonUtil {

	public static Properties jsonToProperties(final JsonObject jsonObject) {
		Properties properties = new Properties();
		BiConsumer<JsonObject, String>[] consumer = new BiConsumer[1];
		consumer[0] = ((json, parentKey) ->
				json.fieldNames().forEach(fieldName -> {
					Object value = json.getValue(fieldName);
					if (value instanceof JsonArray) {
						throw new RuntimeException(String.format("wrong format: JsonArray, key: %s", fieldName));
					} else if (value instanceof JsonObject) {
						consumer[0].accept(ReflectionsUtil.autoCast(value), fieldName + ".");
					} else {
						properties.setProperty(parentKey + fieldName, String.valueOf(value));
					}
				})
			);
		consumer[0].accept(jsonObject, "");
		return properties;
	}

	public static Object getJsonAddableObject(Object object) {
		if (object instanceof Date) {
			return ReflectionsUtil.<Date>autoCast(object).toInstant();
		} else if (object instanceof BigDecimal) {
			return ReflectionsUtil.<BigDecimal>autoCast(object).toPlainString();
		}
		return object;
	}

	public static boolean isJson(String str) {
		return isJsonArray(str) || isJsonObject(str);
	}

	public static boolean isJsonObject(String str) {
		String nvlStr = StringUtil.nvl(str).trim().replaceAll("\\n", "");
		return nvlStr.startsWith("{") && nvlStr.endsWith("}");
	}

	public static boolean isJsonArray(String str) {
		String nvlStr = StringUtil.nvl(str).trim().replaceAll("\\n", "");
		return nvlStr.startsWith("[") && nvlStr.endsWith("]");
	}
}
