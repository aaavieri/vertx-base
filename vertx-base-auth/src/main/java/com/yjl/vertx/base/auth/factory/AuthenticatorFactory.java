package com.yjl.vertx.base.auth.factory;

import com.google.inject.Inject;
import com.yjl.vertx.base.auth.component.AuthenticationCompleteListener;
import com.yjl.vertx.base.auth.component.AuthenticationComponentIf;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.builder.ParamMapBuilder;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.util.FutureUtil;
import com.yjl.vertx.base.web.factory.component.BaseRestRouteFactory;
import com.yjl.vertx.base.web.factory.component.HttpServerFactory;
import com.yjl.vertx.base.web.handler.HandlerWrapper;
import com.yjl.vertx.base.web.util.ContextUtil;
import io.vertx.core.CompositeFuture;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ComponentInitializer(factoryClass = HttpServerFactory.class)
public abstract class AuthenticatorFactory extends BaseRestRouteFactory {
    
    @Inject
    @Config("auth.url")
    private String authUrl;
    
    @Inject(optional = true)
    private AuthenticationComponentIf authenticationComponent;
    
    @Inject(optional = true)
    private Set<AuthenticationCompleteListener> listenerSet = new HashSet<>();
    
    @Override
    protected List<HandlerWrapper> getHandlerWrapperList() {
        return Stream.of(new HandlerWrapper().handler(this::doAuthenticate).descript("auth").autoHandleError(true)
            .handlerClass(this.getClass()).handlerMethod("doAuthenticate").method(HttpMethod.POST).url(this.authUrl)
            .order(-10).regexp(false)).collect(Collectors.toList());
    }
    
    protected void doAuthenticate(RoutingContext context) {
        if (this.authenticationComponent == null) {
            context.fail(new FrameworkException(new RuntimeException("No authenticationComponent found")));
        }
        JsonObject headers = JsonObject.mapFrom(new ParamMapBuilder().buildMultiMap(context.request().headers()).getParamMap());
        JsonObject params = ContextUtil.getAllParams(context);
        context.response().setChunked(true);
        this.authenticationComponent.authenticate(headers, params)
            .compose(result -> CompositeFuture.all(
                this.listenerSet.stream().map(listener -> listener.authenticateComplete(context, result)).collect(Collectors.toList())))
            .compose(compositeFuture -> FutureUtil.handleCompositeFuture(compositeFuture,
                cause -> this.getLogger().warn("Failure occurred in authenticationCompleteListener: {}", cause), null))
            .setHandler(asyncResult -> {
                if (asyncResult.failed()) {
                    this.getLogger().warn("Failure occurred: {}", asyncResult.cause());
                    context.fail(asyncResult.cause());
                } else {
                    context.response().end();
                }
            });
    }
}
