package com.yjl.vertx.base.webclient.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class DataPair {
	private String key;
	private Object value;
}
