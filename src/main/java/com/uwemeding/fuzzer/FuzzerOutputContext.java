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
public class FuzzerOutputContext {

	private final FuzzerOutput[] types;

	public FuzzerOutputContext(FuzzerOutput... types) {
		this.types = types;
	}

	/**
	 * Create the output.
	 *
	 * @param props the properties
	 * @param program the program
	 */
	public void create(Properties props, Program program) {
		for (FuzzerOutput type : types) {
			type.createOutput(props, program);
		}
	}
}
