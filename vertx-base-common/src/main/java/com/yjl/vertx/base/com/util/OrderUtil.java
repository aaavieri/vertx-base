package com.yjl.vertx.base.com.util;

import com.yjl.vertx.base.com.anno.Order;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.ToIntFunction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderUtil {

	public static ToIntFunction<Order> sortOrderDescFunc() {
		return sortOrderFunc(false);
	}

	public static ToIntFunction<Order> sortOrderAscFunc() {
		return sortOrderFunc(true);
	}

	public static ToIntFunction<Order> sortOrderFunc(final boolean asc) {
		return order -> getSortOrder(asc, order);
	}

	public static int getSortOrderDesc(final Order order) {
		return getSortOrder(false, order);
	}

	public static int getSortOrderAsc(final Order order) {
		return getSortOrder(true, order);
	}

	public static int getSortOrder(final boolean asc, final Order order) {
		if (order != null) {
			return asc ? order.value() : 0 - order.value();
		} else {
			return Integer.MIN_VALUE;
		}
	}
}
