package com.yjl.vertx.base.dao.factory.dao;

import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.dao.adaptor.AbstractAdaptor;
import com.yjl.vertx.base.dao.anno.operation.Delete;
import com.yjl.vertx.base.dao.anno.operation.Insert;
import com.yjl.vertx.base.dao.anno.operation.Select;
import com.yjl.vertx.base.dao.anno.operation.Update;
import com.yjl.vertx.base.dao.command.SqlCommand;
import com.yjl.vertx.base.dao.executor.AbstractCommandExecutor;
import io.vertx.core.Future;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvocationHandlerFactory {

	private static Class[] sqlAnnotations = new Class[]{Select.class, Update.class, Insert.class, Delete.class};

	public static InvocationHandler getInvocationHandler(SQLClient sqlClient) {
		return new DaoInvocationHandler(sqlClient);
	}

	static class DaoInvocationHandler implements InvocationHandler {

		private SQLClient sqlClient;

		private Map<Method, AbstractCommandExecutor> executorMap = new HashMap<>();

		private Map<Method, AbstractAdaptor> adaptorMap = new HashMap<>();

		DaoInvocationHandler(SQLClient sqlClient) {
			this.sqlClient = sqlClient;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			List<Annotation> annotationList = Stream.of(method.getAnnotations())
				.filter(annotation -> Arrays.asList(sqlAnnotations).contains(annotation.annotationType()))
				.collect(Collectors.toList());
			Type realReturnType;
			if (annotationList.size() == 0) {
				throw new FrameworkException().message("can not find sql operation annotation on method: " + method.getName());
			} else if (annotationList.size() > 1) {
				throw new FrameworkException().message("multi sql operation annotation found on method: " + method.getName());
			} else if (!(method.getGenericReturnType() instanceof ParameterizedType)) {
				throw new FrameworkException().message("methods in mapper must return io.vertx.core.Future Type: " + method.getName());
			} else {
				realReturnType = ReflectionsUtil.<ParameterizedType>autoCast(method.getGenericReturnType()).getActualTypeArguments()[0];
			}
			Annotation sqlAnnotation = annotationList.get(0);
			String sql = ReflectionsUtil.autoCast(sqlAnnotation.annotationType().getMethod("value").invoke(sqlAnnotation));
			SqlCommand command = SqlCommandFactory.getSqlCommand(sql, method, args);
			if (!this.executorMap.containsKey(method)) {
				AbstractCommandExecutor executor = SqlExecutorFactory.getExecutor(command);
				this.executorMap.put(method, executor);
			}
			if (!this.adaptorMap.containsKey(method)) {
				AbstractAdaptor executor = AdaptorFactory.getAdaptor(command.sqlOperation(), realReturnType);
				this.adaptorMap.put(method, executor);
			}
			Future<Object> future = Future.future();
			this.sqlClient.getConnection(as -> {
				if (as.succeeded()) {
					Future<?> executeFuture = this.executorMap.get(method).execute(as.result(), command);
					executeFuture.setHandler(executeResult -> {
						as.result().close();
						if (executeResult.succeeded()) {
							Object adaptResult = this.adaptorMap.get(method).adapt(executeResult.result());
							future.complete(adaptResult);
						} else {
							future.fail(executeResult.cause());
						}
					});
				} else {
					future.fail(as.cause());
				}
			});
			return future;
		}
	}
}
