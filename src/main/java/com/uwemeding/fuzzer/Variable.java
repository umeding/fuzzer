/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import static com.uwemeding.fuzzer.Node.Type.VARIABLE;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Variable.
 * <p>
 * @author uwe
 */
public class Variable extends Node implements NameBearer, Comparable {

	private final String name;
	private final Class<? extends Number> type;
	private final Number from;
	private final Number to;
	private final Number step;
	private final List<Member> members;
	private boolean calculated;
	private int totalSteps;

	public <T extends Number> Variable(String name, T from, T to, T step) {
		if (name == null) {
			throw new NullPointerException("name cannot be null");
		}
		if (from == null) {
			throw new NullPointerException(name + ": 'from' cannot be null");
		}
		if (to == null) {
			throw new NullPointerException(name + ": 'to' cannot be null");
		}
		if (step == null) {
			throw new NullPointerException(name + ": 'step' cannot be null");
		}

		this.name = name;

		this.type = Number.class;
		this.from = from;
		this.to = to;
		this.step = step;

		this.members = new ArrayList<>();
		this.calculated = false;
	}

	public <T extends Number> Variable(String name, T from, T to) {
		this(name, from, to, 1);
	}

	/**
	 * Get the variable name.
	 * <p>
	 * @return the variable name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Get the member step count.
	 * <p>
	 * @return the step count
	 */
	public int getTotalSteps() {
		if (calculated) {
			return totalSteps;
		}
		throw new FuzzerException("Must first calculate the fuzzy space");
	}

	/**
	 * Get the node type.
	 * <p>
	 * @return variable node
	 */
	@Override
	public Type getNodeType() {
		return VARIABLE;
	}

	/**
	 * Get the variable data type.
	 * <p>
	 * @return the data type
	 */
	public Class<? extends Number> getType() {
		return type;
	}

	/**
	 * Get the range start for this variable.
	 * <p>
	 * @param <T>
	 * @return the start
	 */
	public <T extends Number> T getFrom() {
		return (T) from;
	}

	/**
	 * Get the range end for this variable.
	 * <p>
	 * @param <T>
	 * @return the end
	 */
	public <T extends Number> T getTo() {
		return (T) to;
	}

	/**
	 * Get the step.
	 * <p>
	 * @param <T>
	 * @return the step
	 */
	public <T extends Number> T getStep() {
		return (T) step;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 31 * hash + Objects.hashCode(this.name);
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
		final Variable other = (Variable) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Object o) {
		Variable other = (Variable) o;
		return this.getName().compareTo(other.getName());
	}

	/**
	 * Test if this variable has a fuzzy member.
	 * <p>
	 * @param memberName the fuzzy member name
	 * @return true/false
	 */
	public boolean haveMember(String memberName) {
		return members.stream().anyMatch((member) -> (member.getName().equals(memberName)));
	}

	/**
	 * Get a fuzzy member for the variable.
	 * <p>
	 * @param memberName the member name
	 * @return the fuzzy member
	 */
	public Member getMember(String memberName) {
		for (Member member : members) {
			if (member.getName().equals(memberName)) {
				return member;
			}
		}
		throw new FuzzerException(name + ": member '" + memberName + "' not found");
	}

	/**
	 * Test if we have a hedged member
	 * <p>
	 * @param hedgeName  is the hedge name
	 * @param memberName is the member name
	 * @return true/false
	 */
	public boolean haveMember(String hedgeName, String memberName) {
		String fullName = hedgeName + "$" + memberName;
		return haveMember(fullName);
	}

	/**
	 * Get a hedged member.
	 * <p>
	 * @param hedgeName  is the hedge name
	 * @param memberName is the member name
	 * @return the member
	 */
	public Member getMember(String hedgeName, String memberName) {
		String fullName = hedgeName + "$" + memberName;
		return getMember(fullName);
	}

	/**
	 * Add a fuzzy member to this variable.
	 * <p>
	 * @param member the member
	 * @return the member
	 */
	public Member addMember(Member member) {
		if (haveMember(member.getName())) {
			throw new FuzzerException(member.getName() + ": member already exists of variable '" + name + "'");
		}
		members.add(member);
		return member;
	}

	/**
	 * Add a fuzzy member to this variable.
	 * <p>
	 * @param memberName   member name
	 * @param functionCall the function call
	 * @return the member
	 */
	public Member addMember(String memberName, FunctionCall functionCall) {
		if (haveMember(memberName)) {
			throw new FuzzerException(memberName + ": member already exists of variable '" + name + "'");
		}
		Member member = new Member(memberName, functionCall);
		members.add(member);
		return member;
	}

	/**
	 * Add a fuzzy member to this variable.
	 * <p>
	 * @param memberName the member name
	 * @return the member
	 */
	public Member addMember(String memberName) {
		return addMember(memberName, null);
	}

	/**
	 * Get the fuzzy member names for this variable.
	 * <p>
	 * @return the names
	 */
	public Collection<Member> members() {
		return members;
	}

	public String toLogString() {
		int pos = type.getName().lastIndexOf('.');
		String dataTypeName = type.getName().substring(pos + 1);
		return dataTypeName + " [" + from + ", " + to + "] step by " + step;
	}

	@Override
	public String toString() {
		return "Variable{" + "name=" + name + '}';
	}

	// ========== EVALUATIONS ================================================
	/**
	 * Calculate the full fuzzy space for this variable.
	 */
	public void calculateFuzzySpace() {
		double start = getFrom().doubleValue();
		double stop = getTo().doubleValue();
		double step = getStep().doubleValue();

		// enumerate the variable range
		members().forEach(member -> {
			if (member.haveFunctionCall()) {
				for (double i = start; i <= stop; i += step) {
					member.calculateFunctionAt(i);
				}
			} else {
				member.calculateEndPoints(start, stop);
				for (double i = start; i <= stop; i += step) {
					member.calculateValueAt(i);
				}
			}
		});

		// find the max over all members
		double yMax = Double.MIN_VALUE;
		for (Member member : members()) {
			double memberMax = member.findMax();
			yMax = memberMax > yMax ? memberMax : yMax;
		}

		// normalize exploded values into a byte range
		for (Member member : members()) {
			member.normalizeY(yMax, 255);
			totalSteps = Math.max(totalSteps, member.normalized().size());
		}

		this.calculated = true;
	}

}
