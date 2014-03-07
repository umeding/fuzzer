/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer.rule;

/**
 * A rule term: there are always two.
 *
 * @author uwe
 */
public interface Term {

	Term getP();

	Term getQ();
}
