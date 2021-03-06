package com.yjl.vertx.base.auth.factory;

import com.google.inject.Inject;
import com.yjl.vertx.base.auth.component.AuthorizeCompleteListener;
import com.yjl.vertx.base.auth.component.AuthorizeComponentIf;
import com.yjl.vertx.base.com.anno.component.Config;
import com.yjl.vertx.base.com.builder.ParamMapBuilder;
import com.yjl.vertx.base.com.util.FutureUtil;
import com.yjl.vertx.base.web.factory.component.SpecifiedOrderRestRouteFactory;
import com.yjl.vertx.base.web.handler.HandlerWrapper;
import com.yjl.vertx.base.web.util.ContextUtil;
import io.vertx.core.CompositeFuture;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AuthorizerFactory extends SpecifiedOrderRestRouteFactory {
    
    @Inject(optional = true)
    @Config("auth.skipUrls")
    private JsonArray skipUrls = new JsonArray();

    @Inject
    @Config("auth.url")
    private String authUrl;
    
    @Inject(optional = true)
    private AuthorizeComponentIf authorizeComponent;
    
    @Inject(optional = true)
    private Set<AuthorizeCompleteListener> listenerSet = new HashSet<>();
    
    @Override
    protected List<HandlerWrapper> getHandlerWrapperList() {
        return Stream.of(new HandlerWrapper().handler(this::doAuthorize).descript("auth").autoHandleError(true)
            .handlerClass(this.getClass()).handlerMethod("doAuthorize").method(null).url(".*")
            .order(-1).regexp(true)).collect(Collectors.toList());
    }
    
    protected void doAuthorize(RoutingContext context) {
        if (this.skipUrls.stream().map(String::valueOf).anyMatch(url -> context.request().path().startsWith(url))) {
            context.next();
            return;
        }
        if (this.authUrl.equals(context.request().path()) && context.request().method().equals(HttpMethod.POST)) {
            context.next();
            return;
        }
        JsonObject headers = JsonObject.mapFrom(new ParamMapBuilder().buildMultiMap(context.request().headers()).getParamMap());
        JsonObject params = ContextUtil.getAllParams(context);
        this.authorizeComponent.authorize(context.request().path(), headers, params)
            .compose(result -> CompositeFuture.all(this.listenerSet.stream()
                .map(listener -> listener.authorizeComplete(context, result)).collect(Collectors.toList())))
            .compose(compositeFuture -> FutureUtil.handleCompositeFuture(compositeFuture,
                cause -> this.getLogger().warn("Failure occurred in authenticationCompleteListener: {}", cause), null))
            .setHandler(asyncResult -> {
                if (asyncResult.failed()) {
                    this.getLogger().warn("Failure occurred: {}", asyncResult.cause());
                    context.fail(asyncResult.cause());
                } else {
                    context.next();
                }
            });
    }
}
