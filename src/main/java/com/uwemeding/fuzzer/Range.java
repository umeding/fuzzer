/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;

/**
 *
 * @author uwe
 */
public class Range {

	private final static JexlEngine engine = ExpressionEvalFactory.getInstance();

	private final Expression fromExpr;
	private final Expression toExpr;
	private final Expression funcBody;

	public Range(String fromExpr, String toExpr, String funcBody) {
		this.fromExpr = fromExpr.equals("<") ? null : engine.createExpression(fromExpr);
		this.toExpr = toExpr.equals(">") ? null : engine.createExpression(toExpr);
		this.funcBody = engine.createExpression(funcBody);
	}

	public boolean haveFromExpr() {
		return fromExpr != null;
	}

	public boolean haveToExpr() {
		return toExpr != null;
	}

	public Expression getFromExpr() {
		return fromExpr;
	}

	public Expression getToExpr() {
		return toExpr;
	}

	public Expression getFuncBody() {
		return funcBody;
	}

	@Override
	public String toString() {
		return "Range{" + "fromExpr=" + fromExpr + ", toExpr=" + toExpr + ", funcBody=" + funcBody + '}';
	}

}
