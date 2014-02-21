/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Fuzzy range members.
 *
 * @author uwe
 */
public class PointList implements FuzzyPointEvaluatable {

	private final List<Point> points;

	public PointList() {
		this.points = new ArrayList<>();
	}

	/**
	 * Add a point to the members.
	 *
	 * @param p is the point
	 * @return this member
	 */
	public PointList add(Point p) {
		points.add(p);
		return this;
	}

	/**
	 * Add a point to the members.
	 *
	 * @param x x
	 * @param y y
	 * @return the member
	 */
	public PointList add(Number x, Number y) {
		return add(new Point(x, y));
	}

	/**
	 * Get the size of the point list
	 *
	 * @return the size
	 */
	public int size() {
		return points.size();
	}

	/**
	 * The list of points.
	 *
	 * @return the points
	 */
	public Collection<Point> points() {
		return points;
	}

	@Override
	public Number calculateFuzzyMember(Number step) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getTypeName() {
		return "point list";
	}

	@Override
	public String toLogString() {
		StringBuilder sb = new StringBuilder();
		String delim = "[";
		for (Point p : points()) {
			sb.append(delim).append("{").append(p.getX()).append(",").append(p.getY()).append("}");
			delim = ", ";
		}
		sb.append("]");
		return sb.toString();
	}

}
