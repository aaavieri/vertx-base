package com.yjl.vertx.base.redis.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.util.FutureUtil;
import com.yjl.vertx.base.com.util.StringUtil;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.net.SocketAddress;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import io.vertx.redis.client.Response;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RedisFutureComponent {
    
    @Inject
    private Vertx vertx;
    
    @Inject(optional = true)
    @Config("redis.host")
    private String redisHost = "127.0.0.1";
    
    @Inject(optional = true)
    @Config("redis.port")
    private int redisPort = 6379;
    
    @Inject(optional = true)
    @Config("redis.password")
    private String redisPassword = null;
    
    public Future<Response> hget(String hashKey, String key) {
        return this.executeCommand((redisAPI, asyncResultHandler) -> redisAPI.hget(hashKey, key, asyncResultHandler));
    }

    public Future<Response> hset(String... keyAndValue) {
        return this.executeCommand((redisAPI, asyncResultHandler) -> redisAPI.hset(Arrays.asList(keyAndValue), asyncResultHandler));
    }
    
    public Future<Response> hsetnx(String hashKey, String key, String value) {
        return this.executeCommand((redisAPI, asyncResultHandler) -> redisAPI.hsetnx(hashKey, key, value, asyncResultHandler));
    }

    public Future<Response> hdel(String hashKey, String key) {
        return this.executeCommand((redisAPI, asyncResultHandler) -> redisAPI.hdel(Stream.of(hashKey, key).collect(Collectors.toList()), asyncResultHandler));
    }
    
    protected Future<Response> executeCommand(BiConsumer<RedisAPI, Handler<AsyncResult<Response>>> redisAPIConsumer) {
        return FutureUtil.<Redis>consumer2Future(future -> Redis.createClient(this.vertx, this.getOptions()).connect(future))
            .compose(connection -> FutureUtil.consumer2Future(future -> redisAPIConsumer.accept(RedisAPI.api(connection), future)));
//        Future<Response> future = Future.future();
//        Redis.createClient(this.vertx, this.getOptions()).connect(onConnect -> {
//            if (onConnect.succeeded()) {
//                RedisAPI redis = RedisAPI.api(onConnect.result());
//                redisAPIConsumer.accept(redis, responseAsyncResult -> {
//                    if (responseAsyncResult.failed()) {
//                        future.fail(responseAsyncResult.cause());
//                    } else {
//                        future.complete(responseAsyncResult.result());
//                    }
//                });
//            } else {
//                future.fail(onConnect.cause());
//            }
//        });
//        return future;
    }
    
    protected RedisOptions getOptions() {
        RedisOptions redisOptions = new RedisOptions().setEndpoint(SocketAddress.inetSocketAddress
            (this.redisPort, this.redisHost));
        return StringUtil.isBlank(this.redisPassword) ? redisOptions : redisOptions.setPassword(this.redisPassword);
    }
}
