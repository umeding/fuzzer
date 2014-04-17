/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;

/**
 * Hedge functionality.
 * <p>
 * @author uwe
 */
public class Hedge implements NameBearer {

	private final String name;
	private String arg;
	private String expressionString;
	private Expression expression;

	public Hedge(String name) {
		this.name = name;
	}

	public Hedge(String name, String arg) {
		this(name);
		this.arg = arg;
	}

	public Hedge(String name, String arg, String expressionString) {
		this(name);
		this.arg = arg;
		this.expressionString = expressionString;
		this.expression = ExpressionEvalFactory.getInstance().createExpression(expressionString);
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

	public String getExpressionString() {
		return expressionString;
	}

	public void setExpressionString(String expression) {
		this.expressionString = expression;
		this.expression = ExpressionEvalFactory.getInstance().createExpression(expressionString);
	}

	public Expression getExpression() {
		return expression;
	}

	/**
	 * Calculate a hedged value.
	 * <p>
	 * @param value is the value
	 * @return the hedged value
	 */
	public double calculateValue(double value) {
		JexlContext context = new MapContext();
		context.set(getArg(), value);
		return (double) expression.evaluate(context);
	}

	@Override
	public String toString() {
		return "Hedge{" + "name=" + name + '}';
	}
}
