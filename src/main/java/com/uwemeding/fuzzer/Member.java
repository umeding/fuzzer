/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import static com.uwemeding.fuzzer.Node.Type.MEMBER;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Fuzzy range members.
 *
 * @author uwe
 */
public class Member extends Node implements NameBearer {

	private final String name;
	private final List<Point> points;
	private final FunctionCall functionCall;
	// evals
	private final List<Double> exploded;
	private final List<Integer> normalized;

	public Member(String name, FunctionCall functionCall) {
		this.name = name;
		this.functionCall = functionCall;
		this.points = new ArrayList<>();

		// the exploded space
		this.exploded = new ArrayList<>();
		this.normalized = new ArrayList<>();
	}

	public Member(String name) {
		this(name, null);
	}

	/**
	 * Get the member name
	 *
	 * @return the member name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Get the node type.
	 *
	 * @return MEMBER
	 */
	@Override
	public Type getNodeType() {
		return MEMBER;
	}

	/**
	 * Test if we have a function call.
	 *
	 * @return true/false
	 */
	public boolean haveFunctionCall() {
		return functionCall != null;
	}

	/**
	 * Get the function call for this member.
	 *
	 * @return the function call
	 */
	public FunctionCall getFunctionCall() {
		if (functionCall == null) {
			throw new FuzzerException(name + ": no function call found");
		}
		return functionCall;
	}

	/**
	 * Add a point to the members.
	 *
	 * @param p is the point
	 * @return this member
	 */
	public Member add(Point p) {
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
	public Member add(Number x, Number y) {
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
	public List<Point> points() {
		return points;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 89 * hash + Objects.hashCode(this.name);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Member other = (Member) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		return true;
	}

	public String toLogString() {
		StringBuilder sb = new StringBuilder();
		if (haveFunctionCall()) {
			Function func = getFunctionCall().getFunction();
			sb.append(func.getName()).append("(").append(func.getArgumentName()).append(") -> ");
		}
		sb.append("[");
		String delim = "";
		for (Point p : points()) {
			sb.append(delim).append("{").append(p.getX()).append(",").append(p.getY()).append("}");
			delim = ", ";
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String toString() {
		return "Member{" + "name=" + name + ", points=" + points + ", functionCall=" + functionCall + ", exploded=" + exploded + ", normalized=" + normalized + '}';
	}

	// ================= CALCULAIONS ========================================
	/**
	 * Adjust the point list so we have values at the end points
	 *
	 * @param start start interval
	 * @param stop end interval
	 */
	public void calculateEndPoints(double start, double stop) {

		Point p1 = points.get(0);
		if (p1.getX().doubleValue() != start) {
			points.add(0, new Point(start, p1.getY()));
		}

		int end = points.size();
		Point p2 = points.get(end - 1);
		if (p2.getY().doubleValue() != stop) {
			points.add(end, new Point(stop, p2.getY()));
		}
	}

	/**
	 * Calculate the value of a function at a point
	 *
	 * @parm x is the point
	 */
	void calculateFunctionAt(double x) {
		if (functionCall == null) {
			throw new FuzzerException(name + ": no function defined");
		}
		double value = functionCall.invoke(x);
		exploded.add(value);
	}

	/**
	 * Calculate the value at a particular point.
	 *
	 * @param x the x value
	 */
	void calculateValueAt(double x) {
		int len = points.size();
		for (int i = 1; i < len; i++) {
			Point p1 = points.get(i - 1);
			Point p2 = points.get(i);

			if (x >= p1.getX().doubleValue() && x <= p2.getX().doubleValue()) {
				// calculate the slope intercept form
				double m = (p2.getY().doubleValue() - p1.getY().doubleValue()) / (p2.getX().doubleValue() - p1.getX().doubleValue());
				double b = p1.getY().doubleValue() - m * p1.getX().doubleValue();

				double interpolated = m * x + b;
				exploded.add(interpolated);
				return;
			}
		}
	}

	/**
	 * Find the max.
	 *
	 * @return the max
	 */
	double findMax() {
		double max = Double.MIN_VALUE;
		for (double y : exploded) {
			max = y > max ? y : max;
		}
		return max;
	}

	/**
	 * Create the normalized point set.
	 *
	 * @param yMax the overall max value
	 * @param normMax the normalized max
	 */
	void normalizeY(double yMax, int normMax) {
		for (double y : exploded) {
			int norm = (int) Math.round((y / yMax) * normMax);
			normalized.add(norm);
		}
	}

	/**
	 * Get the exploded values.
	 *
	 * @return the exploded values
	 */
	public Collection<Double> exploded() {
		return exploded;
	}

	/**
	 * Get the normalied values.
	 *
	 * @return the normalized values
	 */
	public List<Integer> normalized() {
		return normalized;
	}
}
