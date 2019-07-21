package com.yjl.vertx.base.com.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

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
		} else if (object instanceof Collection) {
			return ReflectionsUtil.<Collection<?>>autoCast(object).stream()
				.reduce(new JsonArray(), (array, element) -> array.add(getJsonAddableObject(element)), JsonArray::addAll);
		} else if (object != null && object.getClass().isArray()) {
			return Stream.of(object).reduce(new JsonArray(), (array, element) -> array.add(getJsonAddableObject(element)), JsonArray::addAll);
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

	public static JsonObject nvl(JsonObject jsonObject) {
		return jsonObject == null ? new JsonObject() : jsonObject;
	}
	
	public static JsonObject copy(JsonObject to, JsonObject from, String... keys) {
	    Stream.of(keys).forEach(key -> {
	        Object value = from.getValue(key);
	        if (value == null) {
	            to.putNull(key);
            } else {
	            to.put(key, value);
            }
        });
	    return to;
    }

    public static JsonObject extendJsonObject(JsonObject jsonObject) {
        JsonObject extend = new JsonObject();
        BiConsumer<String, JsonObject>[] keyExtend = new BiConsumer[1];
        keyExtend[0] = (parentKey, data) -> data.fieldNames().forEach(fieldName -> {
            Object value = data.getValue(fieldName);
            String absoluteKey = parentKey + fieldName;
            extend.put(absoluteKey, value);
            if (value instanceof JsonObject) {
                keyExtend[0].accept(absoluteKey, ReflectionsUtil.autoCast(value));
            }
        });
        keyExtend[0].accept("", jsonObject);
        return extend;
    }
}
