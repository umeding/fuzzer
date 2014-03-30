/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.lang.reflect.Method;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author uwe
 */
public class ProgramPopulateTest extends TestCase {

	public ProgramPopulateTest(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// test to populate a program
	@Test
	public void testPopulateProgram() {

		Program p = new Program("test");

		p.addHedge("very", "x", "x^2");
		p.addHedge("slightly", "x", "x^1.2&&1.0-x^2.0");

		Function f;
		f = p.addPiecewiseFunction("S", "x");
		f.addParameter("A", "B", "C");

		// Add an external function
		try {
			ExternalFunction extern = p.addExternalFunction("sin", "x");
			extern.setupClassReference("java.lang.Math", "sin");

		} catch (Exception e) {
			fail(e.getMessage());
		}
		Variable v;
		v = p.addInput("d", -3, 3, 1);
		v = p.addInput("x", -10., 10, 2.);

		v = p.addOutput("y", -10., 10, 2.);
		Member m = v.addMember("NM").add(-128, 1.0).add(-64, 1.0).add(-32, 0);
		m = v.addMember("NS").add(-64, 0).add(-32, 1.0).add(0, 0);
		FunctionCall call = new FunctionCall(f).bindParameter(1).bindParameter(2).bindParameter(3);
		v.addMember("Z", call);

		assertEquals("Hedge count mismatch", 2, p.hedges().size());
		assertEquals("Function count mismatch", 2, p.functions().size());
		assertEquals("Input var count mismatch", 2, p.inputs().size());
		assertEquals("Output var count mismatch", 1, p.outputs().size());

		p.dump(System.out);
	}

	// test for duplicate variable detection
	@Test
	public void testDupVariable() {
		try {
			Program p = new Program("test");
			p.addInput("d", -3, 3, 1);
			p.addInput("d", -3, 3, 1);

			fail("Exception expected");
		} catch (FuzzerException e) {

		}
	}

}
