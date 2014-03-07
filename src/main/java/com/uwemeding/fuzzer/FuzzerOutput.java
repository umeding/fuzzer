/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.util.Properties;

/**
 * Manage the output.
 *
 * @author uwe
 */
public interface FuzzerOutput {

	/**
	 * Create an output for a program.
	 *
	 * @param props the properties
	 * @param program the program
	 */
	void createOutput(Properties props, Program program);

}
