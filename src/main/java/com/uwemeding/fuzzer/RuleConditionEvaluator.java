/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */

package com.uwemeding.fuzzer;

/**
 * Evaluate a condition.
 * @author uwe
 */
public interface RuleConditionEvaluator {
	
	/**
	 * Evaluate a condition.
	 * @param node a node 
	 * @return  the evaluated node
	 */
	Node evaluate(Node node);
}
