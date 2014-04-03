/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.util.ArrayList;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Simple JEXL test
 *
 * @author uwe
 */
public class JEXLTest {

	public JEXLTest() {
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

	// Test the power calculation extension
	@Test
	public void testPower() {
		JexlEngine engine = ExpressionEvalFactory.getInstance();
		Expression expr = engine.createExpression("X^2");

		JexlContext context = new MapContext();
		// Don't need this really
		context.set("A", 20);
		context.set("B", 0);

		ArrayList<Double> actualList = new ArrayList<>();
		for (int i = -20; i < 20; i++) {
			context.set("X", i);
			Double result = (Double) expr.evaluate(context);
			actualList.add(result);
			System.out.println(result + ",");
		}

		Double[] actual = actualList.toArray(new Double[0]);
		Double[] expected = {400.0, 361.0, 324.0, 289.0, 256.0, 225.0, 196.0,
			169.0, 144.0, 121.0, 100.0, 81.0, 64.0, 49.0, 36.0, 25.0, 16.0,
			9.0, 4.0, 1.0, 0.0, 1.0, 4.0, 9.0, 16.0, 25.0, 36.0, 49.0, 64.0,
			81.0, 100.0, 121.0, 144.0, 169.0, 196.0, 225.0, 256.0, 289.0,
			324.0, 361.0};
		Assert.assertArrayEquals("results wrong", expected, actual);
	}
}
