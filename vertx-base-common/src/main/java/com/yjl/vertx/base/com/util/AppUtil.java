package com.yjl.vertx.base.com.util;

import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.InetAddress;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppUtil {
    
    public static JsonObject getLocalAddress() {
        InetAddress addr = null;
        String ip = "";
        String address = "";
        try {
            addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
            address = addr.getHostName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject().put("ip", StringUtil.nvl(ip)).put("address", StringUtil.nvl(address));
    }
}
