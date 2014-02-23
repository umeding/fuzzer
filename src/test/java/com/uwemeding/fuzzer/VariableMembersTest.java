/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author uwe
 */
public class VariableMembersTest {

	public VariableMembersTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void memberTest() {
		Variable v = new Variable("a", -20, 20, 1);
		v.addMember("N").add(-20, 1).add(-10, 1).add(0, 0);
		v.addMember("Z").add(-10, 0).add(0, 1).add(10, 0);

		v.calculateFuzzySpace();

		Member m;
		m = v.getMember("N");
		Integer[] actualN = m.normalized().toArray(new Integer[0]);
		Integer[] expectedN = {
			255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 229, 204,
			178, 153, 127, 102, 76, 51, 25, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		Assert.assertArrayEquals("Normalized N invalid", expectedN, actualN);

		m = v.getMember("Z");
		Integer[] actualZ = m.normalized().toArray(new Integer[0]);
		Integer[] expectedZ = {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 50, 76, 101, 127, 153, 178,
			204, 229, 255, 229, 204, 178, 153, 127, 101, 76, 50, 25, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		Assert.assertArrayEquals("Normalized Z invalid", expectedZ, actualZ);
	}

	@Test
	public void piecewiseFunctionTest() {
		System.out.println("piecewise function test");
//    FUNCTION PI
//        FROM <       TO B-A     IS 0
//        FROM B-A     TO B-(A/2) IS 2*((X-2(B-A))/A)^2
//        FROM B-(A/2) TO B+(A/2) IS 1 - 2*((X-B)/A)^2
//        FROM B+(A/2) TO B+A     IS 2*((X-(B+A))/A)^2
//        FROM B+A     TO >        IS 0

		PiecewiseFunction func = new PiecewiseFunction("PI", "x");
		func.addParameter("A", "B");
		func.add("<", "B-A", "0");
		func.add("B-A", "B-(A/2)", "2*((x-2*(B-A))/A)^2");
		func.add("B-(A/2)", "B+(A/2)", "1 - 2*((x-B)/A)^2");
		func.add("B+(A/2)", "B+A", "2*((x-(B+A))/A)^2");
		func.add("B+A", ">", "0");

		FunctionCall call = new FunctionCall(func);
		call.bindParameter(20).bindParameter(0);

		Variable v = new Variable("a", -50, 50, 1);
		Member m = v.addMember("N", call);

		v.calculateFuzzySpace();

		for (Integer i : m.normalized()) {
			System.out.println("---> " + i);
		}
	}
}
