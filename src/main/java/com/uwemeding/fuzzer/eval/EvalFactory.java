/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer.eval;

import org.apache.commons.jexl2.JexlEngine;

/**
 * Expression evaluator helper.
 *
 * @author uwe
 */
public class EvalFactory {

	private final static JexlEngine engine;

	static {
		engine = new JexlEngine(null, new ExtendedJexlEngine(false), null, null);
	}

	/**
	 * Get an instance of the expression evaluator.
	 *
	 * @return the evaluator
	 */
	public static JexlEngine getInstance() {
		return engine;
	}

}
