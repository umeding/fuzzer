/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.util.HashMap;
import java.util.Map;

/**
 * Function reference. The optional parameters are held in an array
 *
 * @author uwe
 */
public class FunctionInstance implements FuzzyPointEvaluatable {

	private final Function function;
	private final Map<String, Number> bindings;

	public FunctionInstance(Function function) {
		if (function == null) {
			throw new NullPointerException("function cannot be null");
		}
		this.function = function;
		this.bindings = new HashMap<>();
	}

	public Function getFunction() {
		return function;
	}

	/**
	 * Bind a parameter number to a function.
	 *
	 * @param number the number
	 * @return this function instance
	 */
	public FunctionInstance bindParameter(Number number) {
		int pos = bindings.size();
		String parameterName = function.getNthParameter(pos);
		bindings.put(parameterName, number);
		return this;
	}

	/**
	 * Get a parameter assignment.
	 *
	 * @param name the parameter name
	 * @return the number
	 */
	public Number getParameter(String name) {
		Number number = bindings.get(name);
		if (number == null) {
			throw new FuzzerException(function.getName() + ": undefined parameter '" + name + "'");
		}
		return number;
	}

	@Override
	public Number calculateFuzzyMember(Number step) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getTypeName() {
		return "function call";
	}

	@Override
	public String toLogString() {
		StringBuilder sb = new StringBuilder();
		sb.append(function.getName());
		String delim = " with ";
		for (String parameter : function.parameters()) {
			String n;
			try {
				n = String.valueOf(getParameter(parameter));
			} catch (FuzzerException r) {
				n = "<undefined>";
			}
			sb.append(delim).append(parameter).append("=").append(n);
			delim = ", ";
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return "FuncRef{" + "name=" + function.getName() + '}';
	}

}
