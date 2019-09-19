package com.yjl.vertx.base.web.util;

import com.yjl.vertx.base.com.util.JsonUtil;
import io.vertx.core.http.HttpServerRequest;
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

    public static String getClientIp(RoutingContext context) {
        HttpServerRequest request = context.request();
        String ip;
        ip = request.getHeader("x-forwarded-for");
        if (isNullIp(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isNullIp(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isNullIp(ip)){
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isNullIp(ip)){
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isNullIp(ip)){
            ip = request.remoteAddress().host();
        }
        if(ip.contains(",")){
            ip=ip.split(",")[0];
        }
        if ("0.0.0.0.0.0.0.1".equals(ip) || "0.0.0.0.0.0.0.1%0".equals(ip)){
            ip = "127.0.0.1";
        }
        return ip;
    }

    private static boolean isNullIp(final String ip){
        return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
    }
}
