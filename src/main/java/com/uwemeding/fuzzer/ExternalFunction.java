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

	private Class externalClass;
	private Method method;

	public ExternalFunction(String name, String argumentName) {
		super(name, argumentName);
	}

	public void setupClassReference(String externalClassReference, String arg) {
//		System.out.println("class: "+externalClassReference+" arg: "+arg);
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
