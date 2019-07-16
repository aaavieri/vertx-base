package com.yjl.vertx.base.web.factory.component;

import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.factory.component.BaseAnnotationComponentFactory;
import com.yjl.vertx.base.com.util.ReflectionsUtil;
import com.yjl.vertx.base.web.exception.ApplicationException;
import com.yjl.vertx.base.web.exception.ApplicationWithDataException;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class DefaultFailureHandlerFactory extends BaseAnnotationComponentFactory {

	@Override
	public void configure() {
		this.bind(TypeLiteral.get(new ParameterizedType() {

			@Override
			public Type[] getActualTypeArguments() {
				return new Type[] {RoutingContext.class};
			}

			@Override
			public Type getRawType() {
				return Handler.class;
			}

			@Override
			public Type getOwnerType() {
				return null;
			}
		})).annotatedWith(Names.named("defaultFailureHandler")).toInstance(ReflectionsUtil.autoCast(this.getDefault()));
	}

	private Handler<RoutingContext> getDefault() {
		return routingContext -> {
			if (routingContext.failure() == null) {
				routingContext.response().end(new JsonObject().put("errMsg", "unknown error occurred").put("success", false).put("errCode", 9).toBuffer());
			} else {
			    this.getLogger().error(routingContext.failure().getMessage(), routingContext.failure());
                if (routingContext.failure() instanceof ApplicationException) {
                    ApplicationException e = ReflectionsUtil.autoCast(routingContext.failure());
                    routingContext.response().end(new JsonObject().put("errMsg", e.message()).put("success", false).put("errCode", e.errCode()).toBuffer());
                } else if (routingContext.failure() instanceof FrameworkException) {
                    FrameworkException e = ReflectionsUtil.autoCast(routingContext.failure());
                    routingContext.response().end(new JsonObject().put("errMsg", e.message()).put("success", false).put("errCode", e.errCode()).toBuffer());
                } else if (routingContext.failure() instanceof ApplicationWithDataException) {
                    ApplicationWithDataException e = ReflectionsUtil.autoCast(routingContext.failure());
                    routingContext.response().end(e.errorData().toBuffer());
                } else {
                    routingContext.response().end(new JsonObject().put("errMsg", routingContext.failure().getMessage()).put("success", false).put("errCode",  -1).toBuffer());
                }
            }
		};
	}
}
