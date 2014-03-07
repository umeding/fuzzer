/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

/**
 * Rule node
 *
 * @author uwe
 */
public interface Node {

	public static enum Type {

		// low level nodes
		VARIABLE,
		MEMBER,
		HEDGE,

		// operations
		HEDGING,
		IN,
		AND,
		OR
	}

	/**
	 * Get the node type.
	 *
	 * @return the node type
	 */
	Type getNodeType();
}
