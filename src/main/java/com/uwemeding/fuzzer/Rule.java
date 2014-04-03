/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import static com.uwemeding.fuzzer.Node.Type.AND;
import static com.uwemeding.fuzzer.Node.Type.IN;
import static com.uwemeding.fuzzer.Node.Type.OR;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Fuzzy set rule.
 * <p>
 * @author uwe
 */
public class Rule {

	private final String name;
	private final Node condition;
	private final Map<Variable, Member> assignments;

	public Rule(String name, Node condition) {
		RuleConditions.checkNode(condition, IN, AND, OR);
		this.name = name;
		this.condition = condition;
		this.assignments = new TreeMap<>();
	}

	/**
	 * Get the rule name.
	 * <p>
	 * @return the rule name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the rule condition.
	 * <p>
	 * @return the condition
	 */
	public Node getCondition() {
		return condition;
	}

	/**
	 * Assign a variable.
	 * <p>
	 * @param var the variable
	 * @param m   the member
	 * @return this rule
	 */
	public Rule assign(Variable var, Member m) {
		assignments.put(var, m);
		return this;
	}

	/**
	 * Get the number if assignments
	 * <p>
	 * @return assignment count
	 */
	public int assignmentCount() {
		return assignments.size();
	}

	/**
	 * Get the assignment variables.
	 * <p>
	 * @return the assignment variables
	 */
	public Collection<Variable> assignmentVariables() {
		return assignments.keySet();
	}

	/**
	 * Get the assigned members;
	 * <p>
	 * @return the assigned members;
	 */
	public Collection<Member> assignmentMembers() {
		return assignments.values();
	}

	/**
	 * Get the member assignment for the variable.
	 * <p>
	 * @param var the variable
	 * @return the member
	 */
	public Member getMember(Variable var) {
		return assignments.get(var);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + Objects.hashCode(this.name);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Rule other = (Rule) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Rule{" + "name=" + name + " cond=" + condition + '}';
	}

}
