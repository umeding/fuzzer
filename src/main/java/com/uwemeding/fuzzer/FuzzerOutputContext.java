/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Create the output.
 *
 * @author uwe
 */
public class FuzzerOutputContext {

	private final List<FuzzerOutput> types;

	public FuzzerOutputContext(FuzzerOutput... types) {
		this.types = Arrays.asList(types);
	}

	/**
	 * Create the output.
	 *
	 * @param outputdir where to write the output
	 * @param program the program
	 */
	public void create(String outputdir, Program program) {
		types.stream().forEach((type) -> type.createOutput(outputdir, program));
	}
}
