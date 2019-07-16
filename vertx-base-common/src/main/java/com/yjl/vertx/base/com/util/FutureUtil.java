package com.yjl.vertx.base.com.util;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FutureUtil {
    public static <T> Future<T> handleCompositeFuture(CompositeFuture compositeFuture, Consumer<Throwable> causeConsumer, T result) {
        if (causeConsumer != null) {
            IntStream.range(0, compositeFuture.size()).filter(compositeFuture::failed).mapToObj(compositeFuture::cause)
                .forEach(causeConsumer);
        }
        if (compositeFuture.failed()) {
            return Future.failedFuture(compositeFuture.cause());
        } else {
            return Future.succeededFuture(result);
        }
    }

    public static <T> Future<T> consumer2Future(Consumer<Future<T>> consumer) {
        Future<T> future = Future.future();
        consumer.accept(future);
        return future;
    }

    public static <T> Future<T> blockCode2Future(Vertx vertx, Supplier<T> supplier) {
        Future<T> retFuture = Future.future();
        blockCode(vertx, retFuture, supplier);
        return retFuture;
    }

    public static <T> void blockCode(Vertx vertx, Future<T> endFuture, Supplier<T> supplier) {
        vertx.executeBlocking(future -> {
            T result = supplier.get();
            future.complete(result);
        }, endFuture);
    }
}
