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
			255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 230, 204,
			179, 153, 128, 102, 77, 51, 26, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		Assert.assertArrayEquals("Normalized N invalid", expectedN, actualN);

		m = v.getMember("Z");
		Integer[] actualZ = m.normalized().toArray(new Integer[0]);
		Integer[] expectedZ = {
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 51, 76, 102, 128, 153, 179,
			204, 230, 255, 230, 204, 179, 153, 128, 102, 76, 51, 25, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		Assert.assertArrayEquals("Normalized Z invalid", expectedZ, actualZ);
	}

	@Test
	public void piecewiseFunctionTest() {
		System.out.println("piecewise function test");

		Variable v = new Variable("a", -50, 50, 1);

//    FUNCTION S
//        FROM < TO A IS 0
//        FROM A TO B IS 2*((X-A)/(C-A))^2
//        FROM B TO C IS 1 - 2*((X-C)/(C-A))^2
//        FROM C TO > IS 1
		PiecewiseFunction s = new PiecewiseFunction("S", "x");
		s.addParameter("A", "B", "C");
		s.add("<", "A", "0");
		s.add("A", "B", "2*(((x-A)/(C-A))^2)");
		s.add("B", "C", "1 - (2*(((x-C)/(C-A))^2))");
		s.add("C", ">", "1");

		FunctionCall sCall = new FunctionCall(s);
		sCall.bindParameter(-30).bindParameter(-20).bindParameter(0);

		Member nb = v.addMember("NB", sCall);

//    FUNCTION PI
//        FROM <       TO B-A     IS 0
//        FROM B-A     TO B-(A/2) IS 2*((X-2(B-A))/A)^2
//        FROM B-(A/2) TO B+(A/2) IS 1 - 2*((X-B)/A)^2
//        FROM B+(A/2) TO B+A     IS 2*((X-(B+A))/A)^2
//        FROM B+A     TO >        IS 0
		PiecewiseFunction pi = new PiecewiseFunction("PI", "x");
		pi.addParameter("A", "B");
		pi.add("<", "B-A", "0");
		pi.add("B-A", "B-(A/2)", "2*(((x-(B-A))/A)^2)");
		pi.add("B-(A/2)", "B+(A/2)", "1 - (2*(((x-B)/A)^2))");
		pi.add("B+(A/2)", "B+A", "2*(((x-(B+A))/A)^2)");
		pi.add("B+A", ">", "0");

		FunctionCall piCall = new FunctionCall(pi);
		piCall.bindParameter(20).bindParameter(0);

		Member ns = v.addMember("NS", piCall);

//	      FUZZY Z   MEMBERS -10,0 0,1 10,0
		Member z = v.addMember("Z").add(-10, 0).add(0, 1).add(10, 0);

//        FUZZY PS  MEMBERS 0,0 15,1 30,0
		Member ps = v.addMember("PS").add(0, 0).add(15, 1).add(30, 0);

//        FUZZY PB  MEMBERS 20,0 30,1 50,1
		Member pb = v.addMember("PB").add(20, 0).add(30, 1).add(50, 1);

		v.calculateFuzzySpace();

		dumpNormalized(nb, ns, z, ps, pb);

		// apply hedge
		JexlEngine engine = ExpressionEvalFactory.getInstance();
		Expression expr = engine.createExpression("x^2");

		JexlContext context = new MapContext();
		for (int i = 0; i < nb.normalized().size(); i++) {
			Number n = nb.normalized().get(i);
			context.set("x", n.doubleValue() / 255.);
			Number nhedge = 255. * (Double) expr.evaluate(context);
			System.out.println(i + " " + n + " -> " + nhedge.intValue());
		}

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
