/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

/**
 * Identifier helper functions.
 *
 * @author uwe
 */
public class IdentifierHelper {

	/**
	 * Test if an identifier is valid.
	 *
	 * @param name the name
	 * @return true/false
	 */
	public static boolean isValid(String name) {
		char[] nameChars = name.toCharArray();
		for (int i = 0; i < nameChars.length; i++) {
			boolean valid = i == 0 ? Character.isJavaIdentifierStart(nameChars[i]) : Character.isJavaIdentifierPart(nameChars[i]);
			if (!valid) {
				return valid;
			}
		}
		return true;
	}
}
