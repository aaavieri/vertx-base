package com.yjl.vertx.base.webclient.enumeration;

public enum ClientInstanceInitLevel {
	SHARE_ALL(1),
	SHARE_CLIENT(2),
	SHARE_METHOD(3),
	SHARE_ACCESS(4),
	INHERIT(5);

	int value;

	private ClientInstanceInitLevel(int value) {
		this.value = value;
	}
}
