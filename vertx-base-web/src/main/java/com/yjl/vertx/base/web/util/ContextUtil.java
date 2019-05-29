package com.yjl.vertx.base.web.util;

import com.yjl.vertx.base.com.util.JsonUtil;
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
    
    public static JsonObject getAllParams(RoutingContext context) {
        return Stream.of(getParam(context), getFormAttrs(context), getJsonBOdy(context))
            .reduce((jsonObject1, jsonObject2) -> jsonObject1.mergeIn(jsonObject2, true))
            .orElse(new JsonObject());
    }
    
    public static JsonObject getParam(RoutingContext context) {
        JsonObject retData = new JsonObject();
        context.request().params().forEach(entry -> retData.put(entry.getKey(), entry.getValue()));
        return retData;
    }
    
    public static JsonObject getFormAttrs(RoutingContext context) {
        JsonObject retData = new JsonObject();
        context.request().formAttributes().forEach(entry -> retData.put(entry.getKey(), entry.getValue()));
        return retData;
    }
    
    public static JsonObject getJsonBOdy(RoutingContext context) {
        JsonObject retData = new JsonObject();
        if (JsonUtil.isJson(context.getBodyAsString())) {
            context.getBodyAsJson().getMap().forEach(retData::put);
        }
        return retData;
    }
}
