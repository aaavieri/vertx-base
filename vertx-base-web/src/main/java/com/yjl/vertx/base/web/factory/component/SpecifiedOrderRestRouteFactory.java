package com.yjl.vertx.base.web.factory.component;

public abstract class SpecifiedOrderRestRouteFactory extends BaseRestRouteFactory {
    protected int calcOrder(int order) {
        return this.minRouteOrder + order;
    }
}
