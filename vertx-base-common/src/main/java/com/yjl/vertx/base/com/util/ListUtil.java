package com.yjl.vertx.base.com.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListUtil {

	public static <T> List<T> nvl(List<T> list) {
		return list == null ? new ArrayList<>() : list;
	}
}
