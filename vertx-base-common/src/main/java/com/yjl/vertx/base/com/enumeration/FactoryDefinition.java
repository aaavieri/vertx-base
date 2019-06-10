package com.yjl.vertx.base.com.enumeration;

import lombok.Getter;

public enum FactoryDefinition {
    VERTICLE_FIRSTCLASS(1),
    VERTICLE_OVERRIDE(3),
    VERTICLE_INITIALIZER(5),
    OTHER_FACTORY_FIRSTCLASS(2),
    OTHER_FACTORY_OVERRIDE(4),
    OTHER_FACTORY_INITIALIZER(6);

    @Getter
    private int order;
    FactoryDefinition(int order) {
        this.order = order;
    }
}
