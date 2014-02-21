/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */

package com.uwemeding.fuzzer;

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
		Variable v = new Variable("a", -3,3,1);
		PointList m = v.addMember("NM", new PointList().add(-128, 1.0).add(-64,1.0).add(-32, 0));
		m = v.addMember("NS", new PointList().add(-64, 0).add(-32, 1.0).add(0, 0));
	}
}
