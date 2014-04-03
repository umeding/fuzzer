/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import com.uwemeding.fuzzer.java.JavaConditionEvaluator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author uwe
 */
public class RuleTest {

	public RuleTest() {
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
	public void testRuleConditions() {
		Variable v1 = new Variable("a", -20, 20, 1);
		Member n1 = v1.addMember("N").add(-20, 1).add(-10, 1).add(0, 0);

		Variable v2 = new Variable("b", -20, 20, 1);
		Member n2 = v2.addMember("Z").add(-20, 1).add(-10, 1).add(0, 0);

		Node cond1 = RuleConditions.createInCondition(v1, n1);
		Node cond2 = RuleConditions.createInCondition(v2, n2);
		Node and = RuleConditions.createAndCondition(cond1, cond2);

		JavaConditionEvaluator eval = new JavaConditionEvaluator();
		Node result = eval.evaluate(and);


		System.out.println("cond: "+and);

	}
}
