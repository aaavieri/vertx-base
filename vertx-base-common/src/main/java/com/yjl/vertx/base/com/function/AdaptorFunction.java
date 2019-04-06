package com.yjl.vertx.base.com.function;

import com.yjl.vertx.base.com.util.ReflectionsUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@FunctionalInterface
public interface AdaptorFunction<T, R> {
	R adapt(T t);

	default boolean isMatch(Type typeInput, Type typeOutput) {
		Type[] actualTypeArguments = ReflectionsUtil.<ParameterizedType>autoCast(this.getClass().getGenericSuperclass())
			.getActualTypeArguments();
		return ReflectionsUtil.compareType(actualTypeArguments[0], typeInput, false)
			&& ReflectionsUtil.compareType(actualTypeArguments[1], typeOutput, false);
	}
}
