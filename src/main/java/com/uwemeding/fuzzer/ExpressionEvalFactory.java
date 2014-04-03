/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import org.apache.commons.jexl2.JexlArithmetic;
import org.apache.commons.jexl2.JexlEngine;

/**
 * Expression evaluator helper.
 * <p>
 * @author uwe
 */
public class ExpressionEvalFactory {

	private final static JexlEngine engine;

	static {
		engine = new JexlEngine(null, new ExtendedJexlEngine(false), null, null);
	}

	/**
	 * Get an instance of the expression evaluator.
	 * <p>
	 * @return the evaluator
	 */
	public static JexlEngine getInstance() {
		return engine;
	}

	/**
	 * Jexl engine extension:
	 * <ul>
	 * <li>Changed ^ operator to calculate the power, instead bitwise xor.</li>
	 * </ul>
	 * <p>
	 * @author uwe
	 */
	private static class ExtendedJexlEngine extends JexlArithmetic {

		public ExtendedJexlEngine(boolean lenient) {
			super(lenient);
		}

		/**
		 * Overridden to calculate pow(left, right).
		 * <p>
		 * @param left  value
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

}
