/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Function definition.
 *
 * @author uwe
 */
public class Function implements NameBearer {

	private final String name;
	private final List<String> arguments;
	private final List<String> parameters;

	public Function(String name) {
		this.name = name;
		this.arguments = new ArrayList<>();
		this.parameters = new ArrayList<>();
	}

	/**
	 * Get the function name.
	 *
	 * @return the function name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Add a function argument.
	 *
	 * @param args the argument(s)
	 */
	public void addArgument(String... args) {

		for (String arg : args) {
			for (String knownArg : arguments) {
				if (arg.equals(knownArg)) {
					throw new FuzzerException(name + ": argument '" + arg + "' already defined");
				}
			}
			arguments.add(arg);
		}
	}

	/**
	 * Get the argument count.
	 *
	 * @return the argument count
	 */
	public int argumentCount() {
		return arguments.size();
	}

	/**
	 * Get the nth argument.
	 *
	 * @param n position
	 * @return the argument
	 */
	public String getNthArgument(int n) {
		return getNth(n, arguments, "arguments");
	}

	/**
	 * Add a function configuration parameter.
	 *
	 * @param paras the parameter definition(s)
	 */
	public void addParameter(String... paras) {

		for (String parameter : paras) {
			for (String knownDef : parameters) {
				if (parameter.equals(knownDef)) {
					throw new FuzzerException(name + ": parameter '" + parameter + "' already defined");
				}
			}
			parameters.add(parameter);
		}
	}

	/**
	 * Get the parameter count.
	 *
	 * @return the parameter count
	 */
	public int parameterCount() {
		return parameters.size();
	}

	/**
	 * Get the nth parameter.
	 *
	 * @param n position
	 * @return the parameter
	 */
	public String getNthParameter(int n) {
		return getNth(n, parameters, "parameters");
	}

	/**
	 * Get the list of parameters
	 *
	 * @return the parameters
	 */
	public Collection<String> parameters() {
		return parameters;
	}

	/**
	 * Get a parameter/argument by its position.
	 *
	 * @param n position
	 * @return the parameter
	 */
	private String getNth(int n, List<String> list, String typeName) {
		if (n < 0) {
			throw new FuzzerException(name + ": " + n + ": " + typeName + " reference must be positive");
		}
		if (n >= list.size()) {
			throw new FuzzerException(name + ": " + n + " illegal " + typeName + " position");
		}
		return list.get(n);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + Objects.hashCode(this.name);
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
		final Function other = (Function) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		return true;
	}

	public String toLogString() {
		StringBuilder sb = new StringBuilder();
		String delim = "(";
		for (String arg : arguments) {
			sb.append(delim).append(arg);
			delim = ", ";
		}
		sb.append(")");

		if (!parameters.isEmpty()) {
			delim = " with parameters (";
			for (String parameter : parameters) {
				sb.append(delim).append(parameter);
				delim = ", ";
			}
			sb.append(")");
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return "Function{" + "name=" + name + '}';
	}
}
