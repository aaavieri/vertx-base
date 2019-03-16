package com.yjl.vertx.base.com.resolver;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ParameterResolveResult {

	@Getter
	private String result;

	@Getter
	private List<String> paramNames = new ArrayList<>();

	public ParameterResolveResult setResult(String result) {
		this.result = result;
		return this;
	}

	public ParameterResolveResult addParamName(String paramName) {
		this.paramNames.add(paramName);
		return this;
	}
}
