/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer.eval;

import com.uwemeding.fuzzer.Program;
import com.uwemeding.fuzzer.Variable;

/**
 *
 * @author uwe
 */
public class ProgramEvaluator {

	private final Program program;

	public ProgramEvaluator(Program program) {
		this.program = program;
		compileProgram();
	}

	/**
	 * Compile the program
	 */
	private void compileProgram() {
		for (Variable var : program.inputs()) {
			var.calculateFuzzySpace();
		}
		for (Variable var : program.outputs()) {
			var.calculateFuzzySpace();
		}
	}

}
