package com.yjl.vertx.base.webclient.generator;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.builder.ParamMapBuilder;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.generator.ProxyGeneratorIf;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.webclient.context.WebClientContext;
import com.yjl.vertx.base.webclient.context.WebClientContextCache;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;

import javax.inject.Named;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

public class DefaultWebClientGenerator implements ProxyGeneratorIf {
    
    @Inject
    private WebClientContextCache webClientContextCache;
    
    public <T> T getProxyInstance(Class<T> clientIf) {
        InvocationHandler invocationHandler = (proxy, method, args) -> {
            WebClientContext webClientContext = this.webClientContextCache.getContext(method);
        
            Map<String, Object> paramMap = new ParamMapBuilder().buildMethodCall(method, args).getParamMap();
            HttpRequest<Buffer> httpRequest = webClientContext.initRequest(paramMap);
            return webClientContext.requestExecutor().execute(httpRequest, method, paramMap)
                .compose(bufferHttpResponse -> {
                    Future<Object> future = Future.future();
                    if (bufferHttpResponse.statusCode() >= 200 && bufferHttpResponse.statusCode() < 300) {
                        Object returnValue = webClientContext.responseAdaptor().adapt(bufferHttpResponse);
                        future.complete(returnValue);
                    } else {
                        future.fail(new FrameworkException().message(bufferHttpResponse.statusMessage()));
                    }
                    return future;
                });
        };
        return ReflectionsUtil.autoCast(Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { clientIf },
            invocationHandler));
    }
}
