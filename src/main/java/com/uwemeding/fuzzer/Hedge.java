/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

/**
 * Hedge functionality.
 *
 * @author uwe
 */
public class Hedge implements NameBearer {

	private final String name;
	private String arg;
	private String expression;

	public Hedge(String name) {
		this.name = name;
	}

	public Hedge(String name, String arg) {
		this(name);
		this.arg = arg;
	}

	public Hedge(String name, String arg, String expression) {
		this(name);
		this.arg = arg;
		this.expression = expression;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getArg() {
		return arg;
	}

	public void setArg(String arg) {
		this.arg = arg;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public String toString() {
		return "Hedge{" + "name=" + name + '}';
	}
}
