/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

/**
 *
 * @author uwe
 */
public class FuzzerException extends RuntimeException {

	public FuzzerException() {
	}

	public FuzzerException(String message) {
		super(message);
	}

	public FuzzerException(String message, Throwable cause) {
		super(message, cause);
	}

	public FuzzerException(Throwable cause) {
		super(cause);
	}

	public FuzzerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
