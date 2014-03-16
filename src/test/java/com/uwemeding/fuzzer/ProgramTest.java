/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import com.uwemeding.fuzzer.eval.ProgramEvaluator;
import com.uwemeding.fuzzer.java.JavaOutputType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author uwe
 */
public class ProgramTest {

	public ProgramTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	Program program;

	@Before
	public void setUp() {
		program = new Program("Demo2");

		program.addHedge("very", "x", "x^2");
		program.addHedge("slightly", "x", "(x^1.2)&&(1.0-(x^2.0))");

		PiecewiseFunction s = new PiecewiseFunction("S", "x");
		s.addParameter("A", "B", "C");
		s.add("<", "A", "0");
		s.add("A", "B", "2*(((x-A)/(C-A))^2)");
		s.add("B", "C", "1 - (2*(((x-C)/(C-A))^2))");
		s.add("C", ">", "1");

		program.addFunction(s);

		PiecewiseFunction pi = new PiecewiseFunction("PI", "x");
		pi.addParameter("A", "B");
		pi.add("<", "B-A", "0");
		pi.add("B-A", "B-(A/2)", "2*(((x-(B-A))/A)^2)");
		pi.add("B-(A/2)", "B+(A/2)", "1 - (2*(((x-B)/A)^2))");
		pi.add("B+(A/2)", "B+A", "2*(((x-(B+A))/A)^2)");
		pi.add("B+A", ">", "0");
		program.addFunction(pi);

		PiecewiseFunction triangle = new PiecewiseFunction("TRIANGLE", "x");
		triangle.addParameter("A", "B", "C");
		triangle.add("<", "A", "0");
		triangle.add("A", "B", "(x - A) / (B - A)");
		triangle.add("B", "C", "1-((x - B)/(C - B))");
		triangle.add("C", ">", "0");
		program.addFunction(triangle);

		// Variable: tta
		Variable tta = program.addInput("tta", -50, 50, 1);
		FunctionCall s1 = new FunctionCall(s);
		s1.bindParameter(-30).bindParameter(-20).bindParameter(10);
		Member tta_nb = tta.addMember("NB", s1);

		FunctionCall pi1 = new FunctionCall(pi);
		pi1.bindParameter(20).bindParameter(0);
		Member tta_ns = tta.addMember("NS", pi1);

		Member tta_z = tta.addMember("Z");
		tta_z.add(-10, 0).add(0, 1).add(10, 0);

		Member tta_ps = tta.addMember("PS");
		tta_ps.add(0, 0).add(15, 1).add(30, 0);

		Member tta_pb = tta.addMember("PB");
		tta_pb.add(20, 0).add(30, 1).add(50, 1);

		// Variable: dTheta
		Variable dth = program.addInput("dTheta", -20, 20, 1);
		Member dth_n = dth.addMember("N");
		dth_n.add(-20, 1).add(-10, 1).add(0, 0);

		Member dth_z = dth.addMember("Z");
		dth_z.add(-10, 0).add(0, 1).add(10, 0);

		FunctionCall tri1 = new FunctionCall(triangle);
		tri1.bindParameter(-10).bindParameter(0).bindParameter(10);
		Member dth_p = dth.addMember("P", tri1);

		// Variable: veloc
		Variable v = program.addOutput("veloc", -5, 5, 0.1);
		Member v_nb = v.addMember("NB").add(-5, 1).add(-2, 1).add(-1, 0);
		Member v_ns = v.addMember("NS").add(-2, 0).add(-1, 1).add(0, 0);
		Member v_z = v.addMember("Z").add(-1, 0).add(0, 1).add(1, 0);
		Member v_ps = v.addMember("PS").add(0, 0).add(1, 1).add(2, 0);
		Member v_pb = v.addMember("PB").add(1, 0).add(2, 1).add(5, 1);

		Node tta_is_pb = Conditions.createInCondition(tta, tta_pb);
		Node dth_is_z = Conditions.createInCondition(dth, dth_z);
		Node and = Conditions.createAndCondition(tta_is_pb, dth_is_z);

		Rule r1 = program.addRule("Rule01", and);
		r1.assign(v, v_z);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testJava() {

		ProgramEvaluator progEval = new ProgramEvaluator(program);
		program.dump(System.out);

		FuzzerOutputContext context = new FuzzerOutputContext(new JavaOutputType());
		context.create(System.getProperties(), program);
	}
}
