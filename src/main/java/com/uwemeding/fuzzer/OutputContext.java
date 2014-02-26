/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.util.Properties;

/**
 * Create the output.
 *
 * @author uwe
 */
public class OutputContext {

	private final OutputStrategy strategy;

	public OutputContext(OutputStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * Create the output.
	 *
	 * @param props the properties
	 * @param program the program
	 */
	public void create(Properties props, Program program) {
		strategy.createOutput(props, program);
	}
}
