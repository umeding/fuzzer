/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

/**
 * A rule expression.
 *
 * @author uwe
 */
public abstract class Expression extends Node {

	/**
	 * Get the left node of the expression.
	 *
	 * @return the left node
	 */
	public abstract Node getLeft();

	/**
	 * Get the right node of the expression.
	 *
	 * @return the right node
	 */
	public abstract Node getRight();

}
