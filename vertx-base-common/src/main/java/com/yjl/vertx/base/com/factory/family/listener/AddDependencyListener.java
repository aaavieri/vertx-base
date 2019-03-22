package com.yjl.vertx.base.com.factory.family.listener;

import com.yjl.vertx.base.com.factory.family.FactoryFamilyNode;

public interface AddDependencyListener {
	void onAdd(FactoryFamilyNode node, FactoryFamilyNode dependencyNode);
}
