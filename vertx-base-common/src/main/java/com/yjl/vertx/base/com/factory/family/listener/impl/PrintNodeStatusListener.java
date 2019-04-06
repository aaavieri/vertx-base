package com.yjl.vertx.base.com.factory.family.listener.impl;

import com.yjl.vertx.base.com.factory.family.FactoryFamilyNode;
import com.yjl.vertx.base.com.factory.family.listener.NodeStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintNodeStatusListener implements NodeStatusListener {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void onStatusChange(int prevStatus, int status, FactoryFamilyNode node) {
		logger.info("status of {} is changed to {}", node.realNode().factoryClass().getSimpleName(), status);
	}
}
