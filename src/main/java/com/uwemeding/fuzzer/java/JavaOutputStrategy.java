/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */

package com.uwemeding.fuzzer.java;

import com.uwemeding.fuzzer.OutputStrategy;
import com.uwemeding.fuzzer.Program;
import java.util.Properties;

/**
 * The JAVA output strategy.
 * @author uwe
 */
public class JavaOutputStrategy implements OutputStrategy {

	@Override
	public void createOutput(Properties props, Program program) {
		System.out.println("Create a java program");
	}
	
}
