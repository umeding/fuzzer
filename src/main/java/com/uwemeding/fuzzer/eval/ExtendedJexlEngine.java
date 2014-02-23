/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer.eval;

import org.apache.commons.jexl2.JexlArithmetic;

/**
 * Jexl engine extension:
 * <ul>
 * <li>Changed ^ operator to calculate the power, instead bitwise xor.</li>
 * </ul>
 *
 * @author uwe
 */
public class ExtendedJexlEngine extends JexlArithmetic {

	public ExtendedJexlEngine(boolean lenient) {
		super(lenient);
	}

	/**
	 * Overridden to calculate pow(left, right).
	 *
	 * @param left value
	 * @param right exponent
	 * @return the power
	 */
	@Override
	public Object bitwiseXor(Object left, Object right) {
		double l = toDouble(left);
		double r = toDouble(right);
		return Math.pow(l, r);
	}
}
