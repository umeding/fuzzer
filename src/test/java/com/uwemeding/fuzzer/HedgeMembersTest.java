/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

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
 *
 * @author uwe
 */
public class HedgeMembersTest {

	public HedgeMembersTest() {
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
		// define some hedges
		Hedge very = new Hedge("very", "x", "x^2");

		Variable v = new Variable("a", -50, 50, 1);
		v.addMember("pb").add(20, 0).add(30, 1).add(50, 1);
		v.addMember(v.getMember("pb").applyHedge(very));

		v.calculateFuzzySpace();

		Member m = v.getMember("very", "pb");
		Integer[] actualHedgedValues = m.normalized().toArray(new Integer[0]);
		Integer[] expectedHedgedValues = {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 3, 10, 23, 41, 64, 92, 125, 163, 207,
			255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
			255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
			255
		};
		Assert.assertArrayEquals("Normalized hedged value invalid", expectedHedgedValues, actualHedgedValues);
	}

	private void dumpNormalized(Member... members) {
		int len = members[0].normalized().size();
		for (int i = 0; i < len; i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(i).append(" ");
			for (Member m : members) {
				sb.append(m.normalized().get(i)).append(" ");
			}
			System.out.println(sb.toString());
		}
	}
}
