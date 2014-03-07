/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import static com.uwemeding.fuzzer.Node.Type.AND;
import static com.uwemeding.fuzzer.Node.Type.IN;
import static com.uwemeding.fuzzer.Node.Type.OR;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Fuzzy set rule.
 *
 * @author uwe
 */
public class Rule {

	private final Node condition;
	private final Map<Variable, Member> assignments;

	public Rule(Node condition) {
		Conditions.checkNode(condition, IN, AND, OR);
		this.condition = condition;
		this.assignments = new TreeMap<>();
	}

	/**
	 * Get the rule condition.
	 *
	 * @return the condition
	 */
	public Node getCondition() {
		return condition;
	}

	/**
	 * Assign a variable.
	 *
	 * @param var the variable
	 * @param m the member
	 * @return this rule
	 */
	public Rule assign(Variable var, Member m) {
		assignments.put(var, m);
		return this;
	}

	/**
	 * Get the number if assignments
	 *
	 * @return assignment count
	 */
	public int assignmentCount() {
		return assignments.size();
	}

	/**
	 * Get the assignment variables.
	 *
	 * @return the assignment variables
	 */
	public Collection<Variable> assignmentVariables() {
		return assignments.keySet();
	}

	/**
	 * Get the member assignment for the variable.
	 *
	 * @param var the variable
	 * @return the member
	 */
	public Member getMember(Variable var) {
		return assignments.get(var);
	}

}
