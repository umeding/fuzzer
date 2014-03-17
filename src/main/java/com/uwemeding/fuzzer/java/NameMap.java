/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer.java;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Name map.
 *
 * @author uwe
 */
public class NameMap extends HashMap<String, String> {

	private final static String BASENAME = "t";
	private final AtomicInteger count;

	public NameMap() {
		this.count = new AtomicInteger();
	}

	/**
	 * Map a variable name
	 *
	 * @param name is the full name
	 * @return the mapped name
	 */
	public String map(String name) {
		String mapped = super.get(name);
		if (mapped == null) {
			mapped = String.format("%s%03d", BASENAME, count.incrementAndGet());
			super.put(name, mapped);
		}
		return mapped;
	}
}
