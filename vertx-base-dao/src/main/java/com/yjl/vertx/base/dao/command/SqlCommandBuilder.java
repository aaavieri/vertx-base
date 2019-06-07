package com.yjl.vertx.base.dao.command;

import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.util.JsonUtil;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.dao.anno.operation.Delete;
import com.yjl.vertx.base.dao.anno.operation.Insert;
import com.yjl.vertx.base.dao.anno.operation.Select;
import com.yjl.vertx.base.dao.anno.operation.Update;
import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Data
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlCommandBuilder {

	private String sql;

	private Method method;

	private Type realReturnType;

	private SqlOperation sqlOperation;

	private final static Pattern FOREACH_PATTERN = Pattern.compile("<foreach(?: (?:(?:collection='(?<collection>\\w+)')|(?:item='(?<item>\\w+)')" +
        "|(?:open='(?<open>.*?)')|(?:close='(?<close>.*?)')|(?:separator='(?<separator>.*?)')))+>(?<content>.*?)</foreach>");

	private final static Pattern PARAM_PATTERN = Pattern.compile("#\\{\\w+\\}(?:\\.#\\{\\w+\\})*");

	private final static Class[] sqlAnnotations = new Class[]{Select.class, Update.class, Insert.class, Delete.class};

	public SqlCommand build(Map<String, Object> paramMap) {

		SqlCommand sqlCommand = new SqlCommand().sqlOperation(this.sqlOperation).returnType(this.realReturnType);

		if (method.getParameterCount() == 0) {
			return sqlCommand.withParams(false).sql(sql);
		}

		List<SqlCommandFragment> foreachFragmentList = new ArrayList<>();

		Matcher foreachMatcher = FOREACH_PATTERN.matcher(sql);
		while (foreachMatcher.find()) {
			SqlCommandFragment foreachFragment = new SqlCommandFragment().start(foreachMatcher.start()).end(foreachMatcher.end())
				.fragment(foreachMatcher.group()).pattern(FOREACH_PATTERN.pattern());

			Object collection = getParam(paramMap, foreachMatcher.group("collection"));
			String replace = getListItemStream(collection)
				.map(item -> {
					Map<String, Object> itemParamMap = new HashMap<>();
					itemParamMap.put(foreachMatcher.group("item"), JsonUtil.getJsonAddableObject(item));
					List<SqlCommandFragment> childFragmentList = getOrdinaryFragment(itemParamMap, foreachMatcher.group("content"), matcher -> true);
					foreachFragment.addChildren(childFragmentList);
					return buildSql(childFragmentList, foreachMatcher.group("content"));
				})
				.collect(Collectors.joining(foreachMatcher.group("separator"), foreachMatcher.group("open"), foreachMatcher.group("close")));
			foreachFragment.replace(replace);
			foreachFragmentList.add(foreachFragment);
		}

		List<SqlCommandFragment> ordinaryFragmentList = getOrdinaryFragment(paramMap, sql, matcher -> {
			int	start = matcher.start();
			return foreachFragmentList.stream().noneMatch(fragment -> fragment.start() < start && fragment.end() > start);
		});

		List<SqlCommandFragment> allFragments = Stream.concat(foreachFragmentList.stream(), ordinaryFragmentList.stream())
			.sorted(Comparator.comparingInt(SqlCommandFragment::start)).collect(Collectors.toList());

		return sqlCommand.withParams(true).sql(allFragments.stream().reduce(sql,
			(tempSql, fragment) -> tempSql.replace(fragment.fragment(), fragment.replace()),
			(tempSql1, tempSql2) -> tempSql1))
			.params(allFragments.stream().flatMap(fragment -> {
				if (fragment.children() == null || fragment.children().isEmpty()) {
					return Stream.of(fragment.param());
				} else {
					return fragment.children().stream().map(SqlCommandFragment::param);
				}
			}).reduce(new JsonArray(), JsonArray::add, JsonArray::addAll));
	}


	private List<SqlCommandFragment> getOrdinaryFragment(Map<String, Object> paramMap, String sql, Predicate<Matcher> filter) {
		List<SqlCommandFragment> ordinaryFragmentList = new ArrayList<>();
		Matcher ordinaryMatcher = PARAM_PATTERN.matcher(sql);
		while (ordinaryMatcher.find()) {
			int start = ordinaryMatcher.start();
			String content = ordinaryMatcher.group();
			boolean filterResult = filter.test(ordinaryMatcher);
			if (filterResult) {
				ordinaryFragmentList.add(new SqlCommandFragment().pattern(PARAM_PATTERN.pattern()).fragment(content).replace("?")
					.start(start).end(ordinaryMatcher.end()).param(this.getParam(paramMap, content)));
			}
		}
		return ordinaryFragmentList;
	}

	private Object getParam(Map<String, Object> paramMap, String paramName) {
		String[] keys = paramName.replaceAll("[#{}]", "").split("\\.");
		String firstFloorKey = keys[0];
		Object firstFloorParam = paramMap.get(firstFloorKey);
		if (keys.length > 1) {
			return IntStream.range(1, keys.length).boxed()
				.reduce(firstFloorParam,
					(prevFloor, index) -> getChildItem(keys[index], prevFloor),
					(someFloor1, someFloor2) -> someFloor1);
		} else {
			return firstFloorParam;
		}
	}

	private Stream<?> getListItemStream(Object param) {
		if (param == null) {
			return Stream.empty();
		} else if (param instanceof Collection) {
			return ReflectionsUtil.<Collection>autoCast(param).stream();
		} else if (param instanceof JsonArray) {
			return ReflectionsUtil.<JsonArray>autoCast(param).stream();
		} else if (param.getClass().isArray()) {
			return Stream.of(ReflectionsUtil.<Object[]>autoCast(param));
		} else {
			return Stream.of(param);
		}
	}

	private Object getChildItem(String key, Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof Map) {
			return JsonUtil.getJsonAddableObject(ReflectionsUtil.<Map>autoCast(object).get(key));
		} else if (object instanceof JsonObject) {
			return JsonUtil.getJsonAddableObject(ReflectionsUtil.<JsonObject>autoCast(object).getValue(key));
		} else {
			throw new FrameworkException().message("not supported type: " + object.getClass().getName());
		}
	}

	private String buildSql(List<SqlCommandFragment> fragments, String content) {
		return fragments.stream().sorted(Comparator.comparingInt(SqlCommandFragment::start))
			.reduce(content,
				(tempContent, sqlCommandFragment) -> tempContent.replace(sqlCommandFragment.fragment(), sqlCommandFragment.replace()),
				(content1, content2) -> content1
			);
	}

	public static SqlCommandBuilder newInstance(Method method) {
		List<Annotation> annotationList = Stream.of(method.getAnnotations())
			.filter(annotation -> Arrays.asList(sqlAnnotations).contains(annotation.annotationType()))
			.collect(Collectors.toList());
		if (annotationList.size() == 0) {
			throw new FrameworkException().message("can not find sql operation annotation on method: " + method.getName());
		} else if (annotationList.size() > 1) {
			throw new FrameworkException().message("multi sql operation annotation found on method: " + method.getName());
		} else if (!(method.getGenericReturnType() instanceof ParameterizedType)) {
			throw new FrameworkException().message("methods in mapper must return io.vertx.core.Future Type: " + method.getName());
		} else {
			ParameterizedType parameterizedType = ReflectionsUtil.autoCast(method.getGenericReturnType());
			if (!parameterizedType.getRawType().equals(Future.class)) {
				throw new FrameworkException().message("methods in mapper must return io.vertx.core.Future Type: " + method.getName());
			}
			Annotation sqlAnnotation = annotationList.get(0);
			SqlCommandBuilder builder = new SqlCommandBuilder().method(method)
				.realReturnType(parameterizedType.getActualTypeArguments()[0]);
			if (sqlAnnotation.annotationType().equals(Select.class)) {
				return builder.sqlOperation(SqlOperation.SELECT).sql(ReflectionsUtil.<Select>autoCast(sqlAnnotation).value());
			} else if (sqlAnnotation.annotationType().equals(Update.class)) {
				return builder.sqlOperation(SqlOperation.UPDATE).sql(ReflectionsUtil.<Update>autoCast(sqlAnnotation).value());
			} else if (sqlAnnotation.annotationType().equals(Insert.class)) {
				return builder.sqlOperation(SqlOperation.INSERT).sql(ReflectionsUtil.<Insert>autoCast(sqlAnnotation).value());
			} else if (sqlAnnotation.annotationType().equals(Delete.class)) {
				return builder.sqlOperation(SqlOperation.DELETE).sql(ReflectionsUtil.<Delete>autoCast(sqlAnnotation).value());
			}
			return builder;
		}
	}
}
