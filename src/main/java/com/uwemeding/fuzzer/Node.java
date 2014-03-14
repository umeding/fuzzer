/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

/**
 * Rule node
 *
 * @author uwe
 */
public abstract class Node {

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
	public abstract Type getNodeType();

	/**
	 * Cast this node to the desired type.
	 * @param <T> the type
	 * @return the casted node
	 */
	public <T extends Node> T cast() {
		return (T)this;
	}
}
