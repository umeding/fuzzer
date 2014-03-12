/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

/**
 * Fuzzy evaluation strategies.
 *
 * @author uwe
 */
public enum ReasoningStrategy {

	MAXDOT("MAX-DOT"),
	MAXMIN("MAX-MIN");

	private final String name;

	private ReasoningStrategy(String name) {
		this.name = name;
	}

	/**
	 * Get the reasoning name.
	 *
	 * @return the reasoning name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the default reasoning strategy.
	 *
	 * @return MAXDOT
	 */
	public static ReasoningStrategy getDefault() {
		return MAXDOT;
	}
}
