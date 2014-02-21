/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Variable.
 *
 * @author uwe
 */
public class Variable implements NameBearer {

	private final String name;
	private final Class<? extends Number> type;
	private final Number from;
	private final Number to;
	private final Number step;
	private final Map<String, FuzzyPointEvaluatable> members;

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

		this.type = from.getClass();
		this.from = from;
		this.to = to;
		this.step = step;

		this.members = new HashMap<>();
	}

	public <T extends Number> Variable(String name, T from, T to) {
		this(name, from, to, 1);
	}

	/**
	 * Get the variable name.
	 *
	 * @return the variable name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Get the variable data type.
	 *
	 * @return the data type
	 */
	public Class<? extends Number> getType() {
		return type;
	}

	/**
	 * Get the range start for this variable.
	 *
	 * @param <T>
	 * @return the start
	 */
	public <T extends Number> T getFrom() {
		return (T)from;
	}

	/**
	 * Get the range end for this variable.
	 *
	 * @param <T>
	 * @return the end
	 */
	public <T extends Number> T getTo() {
		return (T)to;
	}

	/**
	 * Get the step.
	 *
	 * @param <T>
	 * @return the step
	 */
	public <T extends Number> T getStep() {
		return (T)step;
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

	/**
	 * Test if this variable has a fuzzy member.
	 *
	 * @param name the fuzzy member name
	 * @return true/false
	 */
	public boolean haveMember(String name) {
		return members.containsKey(name);
	}

	/**
	 * Get a fuzzy member for the variable.
	 *
	 * @param memberName the member name
	 * @return the fuzzy member
	 */
	public FuzzyPointEvaluatable getMember(String memberName) {
		FuzzyPointEvaluatable content = members.get(memberName);
		if (content == null) {
			throw new FuzzerException(name + ": member '" + memberName + "' not found");
		}
		return content;
	}

	/**
	 * Add a fuzzy member to this variable.
	 *
	 * @param name name of the fuzzy member
	 * @param content the content.
	 */
	public void addMember(String name, FuzzyPointEvaluatable content) {
		members.put(name, content);
	}

	/**
	 * Get the fuzzy member names for this variable.
	 *
	 * @return the names
	 */
	public Collection<String> memberNames() {
		return members.keySet();
	}

	public String toLogString() {
		int pos = type.getName().lastIndexOf('.');
		String dataTypeName = type.getName().substring(pos + 1);
		return dataTypeName + " [" + from + ", " + to + "] step by " + step;
	}
}
