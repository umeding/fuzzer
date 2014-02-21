/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

/**
 * The fuzzy content evaluator.
 *
 * @author uwe
 */
public interface FuzzyPointEvaluatable {

	/**
	 * Calculate a fuzzy point step.
	 *
	 * @param step the step
	 * @return the value at the step
	 */
	Number calculateFuzzyMember(Number step);

	/**
	 * Type name.
	 *
	 * @return the type name
	 */
	String getTypeName();

	/**
	 * Get a loggable string for this.
	 *
	 * @return a string
	 */
	String toLogString();
}
