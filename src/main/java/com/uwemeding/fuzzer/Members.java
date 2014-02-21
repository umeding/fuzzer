/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */

package com.uwemeding.fuzzer;

import java.util.ArrayList;

/**
 * Fuzzy range members.
 * @author uwe
 */
public class Members extends ArrayList<Point> implements FuzzyPointEvaluatable {

	public Members() {
	}

	@Override
	public Number calculateFuzzyMember(Number step) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
