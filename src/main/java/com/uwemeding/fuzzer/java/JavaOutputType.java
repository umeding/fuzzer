/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer.java;

import com.uwemeding.fuzzer.ConditionEvaluator;
import com.uwemeding.fuzzer.FuzzerException;
import com.uwemeding.fuzzer.FuzzerOutput;
import com.uwemeding.fuzzer.Member;
import com.uwemeding.fuzzer.Program;
import com.uwemeding.fuzzer.Variable;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * The JAVA output strategy.
 *
 * @author uwe
 */
public class JavaOutputType implements FuzzerOutput {

	@Override
	public ConditionEvaluator getConditionEvaluator() {
		return new JavaConditionEvaluator();
	}

	@Override
	public void createOutput(Properties props, Program program) {
		System.out.println("Create a java program");
		try {
			createProgram(program);
		} catch (IOException ex) {
			throw new FuzzerException(ex);
		}
	}

	private void createProgram(Program program) throws IOException {
		Java.CLASS clazz = Java.createClass("public", program.getName());
		clazz.setPackage("com.foo");

		Java.METHOD ctor = clazz.addCTOR("public");
		ctor.setComment("Construct a fuzzy object");

		Java.METHOD method = clazz.addMETHOD("public", "void", "calculate");
		method.setComment("Calculate new values");

		addIntegerRoundingMethod(clazz);
		addReasoningMethod(clazz, program);

		// inner class
		Java.CLASS crispClass = clazz.addCLASS("public static", "CRISP");
		Java.METHOD crispCtor = crispClass.addCTOR("private");
		for (Variable v : program.outputs()) {
			crispClass.addReadOnlyProperty(v.getName(), v.getType().getName(), null);
		}

		for (Variable v : program.inputs()) {
			for (Member m : v.members()) {
				String varName = v.getName() + "$" + m.getName();
				String content = createNormalized(m);
				Java.VAR var = clazz.addVAR("private final", "int[]", varName, content);
				var.setComment("Variable: " + v.getName() + " Member: " + m.getName());
			}
		}
		Java.setBaseDirectory(new File("./"));
		Java.createSource(clazz);
	}

	/**
	 * Add the integer rounding method to the generated code.
	 *
	 * @param clazz the class wrapper
	 */
	private void addIntegerRoundingMethod(Java.CLASS clazz) {
		// round method
		Java.METHOD iround = clazz.addMETHOD("private", "int", "iround");
		iround.setComment("Round to the nearest integer");
		iround.addArg("Number", "value", "value to be rounded");
		iround.addRETURN("(int)Math.round(value.doubleValue())");
	}

	/**
	 * Add the reasoning method to the generated code.
	 *
	 * @param clazz the class wrapper
	 * @param program the fuzzy program
	 * @throws FuzzerException
	 */
	private void addReasoningMethod(Java.CLASS clazz, Program program) throws FuzzerException {
		// reasoning strategy
		Java.METHOD rs = clazz.addMETHOD("private", "Number", "rs");
		rs.setComment("Reasoning strategy is " + program.getReasoningStrategy().getName());
		rs.addArg("Number", "a", "strength value");
		rs.addArg("Number", "b", "mapped value");
		switch (program.getReasoningStrategy()) {
			case MAXDOT:
				rs.addRETURN("a.doubleValue() * b.doubleValue()");
				break;
			case MAXMIN:
				rs.addRETURN("a.doubleValue() < b.doubleValue() ? a : b");
				break;
			default:
				throw new FuzzerException(program.getReasoningStrategy() + ": not implemented");

		}
	}

	private String createNormalized(Member m) {
		StringBuilder sb = new StringBuilder();

		int count = 0;
		String delim = "";
		sb.append("{");
		for (Integer i : m.normalized()) {
			if (count++ % 10 == 0) {
				sb.append(delim).append("\n        ");
			} else {
				sb.append(delim);
			}
			sb.append(String.format(" %3d", i));
			delim = ",";
		}
		sb.append("\n    }");
		return sb.toString();
	}

}
