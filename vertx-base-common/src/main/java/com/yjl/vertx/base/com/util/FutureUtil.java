package com.yjl.vertx.base.com.util;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FutureUtil {
    public static <T> Future<T> handleCompositeFuture(CompositeFuture compositeFuture, Consumer<Throwable> causeConsumer, T result) {
        if (causeConsumer != null) {
            IntStream.range(0, compositeFuture.size()).filter(compositeFuture::failed).mapToObj(compositeFuture::cause)
                .forEach(causeConsumer);
        }
        Future<T> future = Future.future();
        if (compositeFuture.failed()) {
            future.fail(compositeFuture.cause());
        } else {
            future.complete(result);
        }
        return future;
    }
}
