/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;

/**
 * Piecewise function.
 *
 * @author uwe
 */
public class PiecewiseFunction extends Function {

	private final List<Range> ranges;

	public PiecewiseFunction(String name, String argumentName) {
		super(name, argumentName);
		this.ranges = new ArrayList<>();
	}

	public PiecewiseFunction add(String from, String to, String func) {
		Range range = new Range(from, to, func);
		ranges.add(range);
		return this;
	}

	/**
	 * {@inheritDoc }
	 *
	 * @param call the function call parameters
	 * @param arg the function argument
	 * @return the value
	 */
	@Override
	public double call(FunctionCall call, double arg) {

		// set all the parameters
		JexlContext context = new MapContext();
		for (String parameterName : call.parameterNames()) {
			context.set(parameterName, call.getParameter(parameterName).doubleValue());
		}
		// set the argument value
		context.set(call.getFunction().getArgumentName(), arg);

		// find the appropriate range first
		Expression funcBody = null;
		for (Range r : ranges) {
			boolean fromMatch;
			boolean toMatch;

			Double fromValue = 0.0;
			if (r.haveFromExpr()) {
				fromValue = (Double) r.getFromExpr().evaluate(context);
				fromMatch = arg >= fromValue;
			} else {
				fromMatch = true;
			}

			Double toValue = 0.0;
			if (r.haveToExpr()) {
				toValue = (Double) r.getToExpr().evaluate(context);
				toMatch = arg <= toValue;
			} else {
				toMatch = fromMatch;
			}

			if (fromMatch && toMatch) {
				String from = r.getFromExpr() == null ? "<" : r.getFromExpr().getExpression();
				String to = r.getToExpr() == null ? ">" : r.getToExpr().getExpression();
				System.out.print("match " + from + "->" + fromValue + " .. " + to + "->" + toValue + " for " + arg);
				funcBody = r.getFuncBody();
				break;
			}
		}
		// now evaluate the function body
		if (funcBody == null) {
			throw new FuzzerException(call.getFunction().getName() + ": no range found for " + arg);
		}
		Object result = funcBody.evaluate(context);
		System.out.println(" "+funcBody.getExpression()+" -> "+result);
		if (result instanceof Number) {
			return ((Number) result).doubleValue();
		} else {
			throw new FuzzerException(getName() + ": unable to evaluate to number '" + funcBody.getExpression() + "'");
		}
	}
}
