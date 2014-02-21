/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author uwe
 */
public class VariableIterationTest {

	public VariableIterationTest() {
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
	public void integerIteration() {
		Variable var = new Variable("d", -3, 3, 1);

		int start = var.getFrom();
		int stop = var.getTo();
		int step = var.getStep();

		ArrayList<Integer> list = new ArrayList<>();
		for (int i = start; i < stop; i += step) {
			list.add(i);
		}

		Integer[] actual = list.toArray(new Integer[0]);
		Integer[] expected = {-3, -2, -1, 0, 1, 2};
		assertArrayEquals("int step array mismatch", expected, actual);
	}

	@Test
	public void doubleIteration() {
		Variable var = new Variable("d", -1., 2., 0.2);

		double start = var.getFrom();
		double stop = var.getTo();
		double step = var.getStep();

		ArrayList<Double> list = new ArrayList<>();
		for (double d = start; d < stop; d += step) {
			list.add(d);
		}

		System.out.println(list);
		double e = 0.0001;
		Double[] expected = {-1.0, -0.8, -0.6, -0.4, -0.2, 0., 0.2, 0.4, 0.6, 0.8, 1.0, 1.2, 1.4, 1.6, 1.8, 2.0};
		int i = 0;
		for (double d : list) {
			if (!isWithinError(expected[i++], d, e)) {
				fail("step " + i + " " + expected[i] + " != " + d);
			}
		}
	}

	private boolean isWithinError(double expected, double actual, double error) {
		return Math.abs(expected - actual) < error;
	}
}
