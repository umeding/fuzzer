/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

/**
 * Manage the output.
 *
 * @author uwe
 */
@FunctionalInterface
public interface FuzzerOutput {

	/**
	 * Create an output for a program.
	 *
	 * @param output where to create the output
	 * @param program the program
	 */
	void createOutput(String output, Program program);

}
