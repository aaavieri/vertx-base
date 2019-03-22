package com.yjl.vertx.base.com.factory.family.listener;

import com.yjl.vertx.base.com.factory.family.FactoryFamilyNode;

public interface NodeStatusListener {
	void onStatusChange(int prevStatus, int status, FactoryFamilyNode node);
}
