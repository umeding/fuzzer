/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Function reference. The optional parameters are held in an array
 *
 * @author uwe
 */
public class FunctionCall {

	private final Function function;
	private final Map<String, Number> bindings;

	public FunctionCall(Function function) {
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
	public FunctionCall bindParameter(Number number) {
		int pos = bindings.size();
		String parameterName = function.getNthParameter(pos);
		bindings.put(parameterName, number);
		return this;
	}

	/**
	 * Get the parameter names.
	 *
	 * @return the parameter names
	 */
	public Collection<String> parameterNames() {
		return bindings.keySet();
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

	/**
	 * Invoke the function.
	 *
	 * @param arg is the argument
	 * @return the value at arg
	 */
	public double invoke(double arg) {
		return function.call(this, arg);
	}

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
