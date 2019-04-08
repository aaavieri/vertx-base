package com.yjl.vertx.base.web.util;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContextUtil {
    
    public static RoutingContext cpJsonToContext(RoutingContext routingContext, JsonObject jsonObject, String... keys) {
        Stream.of(keys).forEach(key -> routingContext.put(key, jsonObject.getValue(key)));
        return routingContext;
    }
    
    public static <T> JsonObject cpContextToJson(JsonObject jsonObject, RoutingContext routingContext, String... keys) {
        Stream.of(keys).forEach(key -> {
            T t = routingContext.get(key);
            if (t == null) {
                jsonObject.putNull(key);
            } else {
                jsonObject.put(key, t);
            }
        });
        return jsonObject;
    }
    
    public static JsonObject cpContextStrToJson(JsonObject jsonObject, RoutingContext routingContext, String... keys) {
        return ContextUtil.<String>cpContextToJson(jsonObject, routingContext, keys);
    }
}
