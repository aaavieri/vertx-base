package com.yjl.vertx.base.dao.generator;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.builder.ParamMapBuilder;
import com.yjl.vertx.base.com.generator.ProxyGeneratorIf;
import com.yjl.vertx.base.com.util.FutureUtil;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.dao.command.SqlCommand;
import com.yjl.vertx.base.dao.context.DaoContext;
import com.yjl.vertx.base.dao.context.DaoContextCache;
import io.vertx.core.Future;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class DefaultDaoGenerator implements ProxyGeneratorIf {
    
    @Inject
    private SQLClient sqlClient;
    
    @Inject
    private DaoContextCache daoContextCache;
    
    @Override
    public <T> T getProxyInstance(Class<T> daoIf) {
        InvocationHandler invocationHandler = (proxy, method, args) -> {
            DaoContext daoContext = this.daoContextCache.getDaoContext(method);
            SqlCommand command = daoContext.sqlCommandBuilder().build(new ParamMapBuilder().buildMethodCall(method, args).getParamMap());

            return FutureUtil.<SQLConnection>consumer2Future(future -> this.sqlClient.getConnection(future))
                .compose(sqlConnection ->
                    ReflectionsUtil.<Future<?>>autoCast(daoContext.sqlCommandExecutor().execute(sqlConnection, command)).compose(result -> {
                        sqlConnection.close();
                        return Future.succeededFuture(daoContext.daoAdaptor().adapt(result));
                    })
                );
//            Future<Object> future = Future.future();
//            this.sqlClient.getConnection(as -> {
//                if (as.succeeded()) {
//                    try {
//                        Future<?> executeFuture = daoContext.sqlCommandExecutor().execute(as.result(), command);
//                        executeFuture.setHandler(executeResult -> {
//                            try {
//                                as.result().close();
//                                if (executeResult.succeeded()) {
//                                    Object adaptResult = daoContext.daoAdaptor().adapt(executeResult.result());
//                                    future.complete(adaptResult);
//                                } else {
//                                    future.fail(executeResult.cause());
//                                }
//                            } catch (Throwable throwable) {
//                                future.fail(throwable);
//                            }
//                        });
//                    } catch (Throwable throwable) {
//                        future.fail(throwable);
//                    }
//                } else {
//                    future.fail(as.cause());
//                }
//            });
//            return future;
        };
        return ReflectionsUtil.autoCast(Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { daoIf },
            invocationHandler));
    }
}
