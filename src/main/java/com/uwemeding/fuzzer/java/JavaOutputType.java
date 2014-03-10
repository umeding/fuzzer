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

		for (Variable v : program.inputs()) {
			for (Member m : v.members()) {
				String varName = v.getName() + "$" + m.getName();
				String content = createNormalized(m);
				Java.VAR var = clazz.addVAR("private final", "int[]", varName, content);
				var.setComment("Variable: "+v.getName()+" Member: "+m.getName());
			}
		}
		Java.setBaseDirectory(new File("./"));
		Java.createSource(clazz);
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
