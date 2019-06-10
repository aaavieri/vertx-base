package com.yjl.vertx.base.com.factory.family;

import com.google.inject.Injector;
import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.enumeration.FactoryDefinition;
import com.yjl.vertx.base.com.factory.component.BaseComponentFactory;
import com.yjl.vertx.base.com.factory.family.listener.NodeStatusListener;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(fluent = true)
public class FactoryFamilyNode {

	public static int STATUS_WAITING_CONFIGURE = 0;

	public static int STATUS_INSTANCE_INIT_DONE = 1;

	public static int STATUS_BEFORE_CONFIGURE = 2;

	public static int STATUS_COMPLETE_CONFIGURE = 3;

	public static int STATUS_AFTER_CONFIGURE = 4;

	public static int STATUS_STOPPED = 5;

	@Getter
	@Setter
	private ComponentInitializer realNode;
	
	@Getter
    @Setter
    private FactoryDefinition definition;

	@Getter
	@Setter
	private FactoryFamily family;

	@Getter
    @Setter
	private int layer;

	@Getter
	private int status = STATUS_WAITING_CONFIGURE;

	@Setter
	@Getter
	private Injector injector;

	@Getter
	@Setter
	private List<NodeStatusListener> nodeStatusListeners;

	private BaseComponentFactory nodeInstance;

//	public FactoryFamilyNode layer(int layer) {
//		this.layer = Math.max(this.layer, layer);
//		return this;
//	}

	public void beforeStart() {
		this.nodeInstance.beforeConfigure();
		this.callNodeListener(this.status, STATUS_BEFORE_CONFIGURE);
	}

	public void startCompleted() {
		this.callNodeListener(this.status, STATUS_COMPLETE_CONFIGURE);
	}

	public void afterStart() {
		this.nodeInstance.afterConfigure();
		this.callNodeListener(this.status, STATUS_AFTER_CONFIGURE);
	}

	public void stop() {
		this.nodeInstance.stop();
		this.callNodeListener(this.status, STATUS_STOPPED);
	}

	private void callNodeListener(int prevStatus, int status) {
		this.status = status;
		this.nodeStatusListeners.forEach(listener -> listener.onStatusChange(prevStatus, status, this));
	}

	public BaseComponentFactory nodeInstance() {
		return this.nodeInstance;
	}

	public FactoryFamilyNode nodeInstance(BaseComponentFactory nodeInstance) {
		this.nodeInstance = nodeInstance;
		if (nodeInstance != null) {
			int prevStatus = this.status;
			this.status = STATUS_INSTANCE_INIT_DONE;
			this.nodeStatusListeners.forEach(listener -> listener.onStatusChange(prevStatus, this.status, this));
		}
		return this;
	}

	public boolean equals(Object o) {
		if (!(o instanceof FactoryFamilyNode)) {
			return false;
		} else {
			FactoryFamilyNode other = (FactoryFamilyNode)o;
			return this.realNode.equals(other.realNode);
		}
	}

	@Override
	public int hashCode() {
		return this.realNode.hashCode();
	}
}
