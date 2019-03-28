package com.yjl.vertx.base.dao.command;

import com.yjl.vertx.base.com.util.JsonUtil;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.dao.anno.operation.SqlParam;
import io.vertx.core.json.JsonObject;
import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

public class SqlCommandParamMapBuilder {

	@Getter
	private Map<String, Object> paramMap = new HashMap<>();

	public SqlCommandParamMapBuilder buildMethodCall(Method method, Object[] params) {

		Function<Integer, Parameter> getParamFunc = i -> method.getParameters()[i % method.getParameterCount()];
		Function<Integer, Object> getParamValueFunc = i -> params[i % method.getParameterCount()];

		IntStream.range(0, 2 * method.getParameterCount())
			.peek(i -> {
				if (i < method.getParameterCount()) {
					if (params[i] instanceof Map) {
						this.buildMap(ReflectionsUtil.autoCast(params[i]));
					} else if (params[i] instanceof JsonObject) {
						this.buildJsonObject(ReflectionsUtil.autoCast(params[i]));
					}
				}
			})
			.filter(i -> (i < method.getParameterCount() && getParamFunc.apply(i).isAnnotationPresent(SqlParam.class))
				|| (i > method.getParameterCount() && getParamFunc.apply(i).isNamePresent()))
			.mapToObj(i -> {
				if (i < method.getParameterCount()) {
					return new AbstractMap.SimpleImmutableEntry<>(getParamFunc.apply(i).getAnnotation(SqlParam.class).value(), getParamValueFunc.apply(i));
				} else {
					return new AbstractMap.SimpleImmutableEntry<>(getParamFunc.apply(i).getName(), getParamValueFunc.apply(i));
				}
			})
			.forEach(this::buildEntry);
		return this;
	}

	public SqlCommandParamMapBuilder buildJsonObject(JsonObject jsonObject) {
		jsonObject.forEach(this::buildEntry);
		return this;
	}

	public SqlCommandParamMapBuilder buildMap(Map<String, Object> map) {
		map.entrySet().forEach(this::buildEntry);
		return this;
	}

	public SqlCommandParamMapBuilder buildEntry(Map.Entry<String, Object> entry) {
		return this.buildItem(entry.getKey(), entry.getValue());
	}

	public SqlCommandParamMapBuilder buildItem(String key, Object value) {
		this.paramMap.put(key, JsonUtil.getJsonAddableObject(value));
		return this;
	}
}