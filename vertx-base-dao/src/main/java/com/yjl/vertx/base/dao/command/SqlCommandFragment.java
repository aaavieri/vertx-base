package com.yjl.vertx.base.dao.command;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Accessors(fluent = true)
public class SqlCommandFragment {

	private int start;

	private int end;

	private String pattern;

	private String fragment;

	private String replace;

	private Object param;

	private List<SqlCommandFragment> children;

	public SqlCommandFragment addChildren(List<SqlCommandFragment> list) {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		this.children.addAll(list);
		return this;
	}

	public SqlCommandFragment addChild(SqlCommandFragment fragment) {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		this.children.add(fragment);
		return this;
	}
}
