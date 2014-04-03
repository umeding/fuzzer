/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */

package com.uwemeding.fuzzer.java;

import com.uwemeding.fuzzer.RuleConditionEvaluator;
import com.uwemeding.fuzzer.Node;

/**
 * Evaluate rule conditions for java.
 * @author uwe
 */
public class JavaConditionEvaluator implements RuleConditionEvaluator {

	public JavaConditionEvaluator() {
	}

	@Override
	public Node evaluate(Node node) {

		switch(node.getNodeType()) {
			case AND:
			case OR:
//				Condition cond = (Condition) node;
				

		}

		return node;
	}
	
}
