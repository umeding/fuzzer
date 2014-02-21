/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

/**
 * A point.
 *
 * @author uwe
 */
public class Point {

	private final Number x;
	private final Number y;

	public Point(Number x, Number y) {
		this.x = x;
		this.y = y;
	}

	public Number getX() {
		return x;
	}

	public Number getY() {
		return y;
	}

	@Override
	public String toString() {
		return "Point{" + "x=" + x + ", y=" + y + '}';
	}

}
