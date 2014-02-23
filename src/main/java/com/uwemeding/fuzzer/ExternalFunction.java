/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.lang.reflect.Method;

/**
 * An external (unary) function.
 *
 * @author uwe
 */
public class ExternalFunction extends Function {

	private final Class klass;
	private final Method method;

	public ExternalFunction(Class klass, Method method) {
		super(klass.getName() + "#" + method.getName(), "x");
		this.klass = klass;
		this.method = method;
	}

	public Class getKlass() {
		return klass;
	}

	public Method getMethod() {
		return method;
	}

	/**
	 * {@inheritDoc }
	 *
	 * @param call the function call parameters
	 * @param arg the function argument
	 * @return the result
	 */
	@Override
	public double call(FunctionCall call, double arg) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
