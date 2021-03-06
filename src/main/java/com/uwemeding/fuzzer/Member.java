/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import static com.uwemeding.fuzzer.Node.Type.MEMBER;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;

/**
 * Fuzzy range members.
 * <p>
 * @author uwe
 */
public class Member extends Node implements NameBearer {

	private final String name;
	private final List<Point> points;
	private final FunctionCall functionCall;
	private final Hedge hedge;
	// evals
	private final List<Double> exploded;
	private final List<Integer> normalized;
	//
	private int referenceCount;

	protected Member(String name, FunctionCall functionCall, Hedge hedge) {
		this.name = name;
		this.functionCall = functionCall;
		this.hedge = hedge;
		this.points = new ArrayList<>();

		// the exploded space
		this.exploded = new ArrayList<>();
		this.normalized = new ArrayList<>();

		// not referenced to start with
		this.referenceCount = 0;
	}

	/**
	 * Create a member with a function call.
	 * <p>
	 * @param name         the member name
	 * @param functionCall the function call
	 */
	public Member(String name, FunctionCall functionCall) {
		this(name, functionCall, null);
	}

	/**
	 * Create a simple member.
	 * <p>
	 * @param name member name
	 */
	public Member(String name) {
		this(name, null);
	}

	/**
	 * Apply a hedge to the member.
	 * <p>
	 * @param hedge is the hedge
	 * @return the hedged member
	 */
	public Member applyHedge(Hedge hedge) {
		String newMemberName = hedge.getName() + "$" + name;
		Member member = new Member(newMemberName, functionCall, hedge);
		this.points.stream().forEach(point -> member.points.add(point));
		return member;
	}

	/**
	 * Get the member name
	 * <p>
	 * @return the member name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Get the node type.
	 * <p>
	 * @return MEMBER
	 */
	@Override
	public Type getNodeType() {
		return MEMBER;
	}

	/**
	 * Test if we have a function call.
	 * <p>
	 * @return true/false
	 */
	public boolean haveFunctionCall() {
		return functionCall != null;
	}

	/**
	 * Get the function call for this member.
	 * <p>
	 * @return the function call
	 */
	public FunctionCall getFunctionCall() {
		if (functionCall == null) {
			throw new FuzzerException(name + ": no function call found");
		}
		return functionCall;
	}

	/**
	 * Test if we have a hedge.
	 * <p>
	 * @return true/false
	 */
	public boolean haveHedge() {
		return hedge != null;
	}

	/**
	 * Get the hedge for this member,
	 * <p>
	 * @return the hedge
	 */
	public Hedge getHedge() {
		if (hedge == null) {
			throw new FuzzerException(name + ": no hedge found");
		}
		return hedge;
	}

	/**
	 * Indicate if this member is referenced.
	 * <p>
	 * @return true/false
	 */
	public boolean isReferenced() {
		return referenceCount > 0;
	}

	/**
	 * Increment the reference counter.
	 */
	public void incrReferenceCount() {
		this.referenceCount++;
	}

	/**
	 * Get the member reference count.
	 * <p>
	 * @return the reference count
	 */
	public int getReferenceCount() {
		return referenceCount;
	}

	/**
	 * Add a point to the members.
	 * <p>
	 * @param p is the point
	 * @return this member
	 */
	public Member add(Point p) {
		points.add(p);
		return this;
	}

	/**
	 * Add a point to the members.
	 * <p>
	 * @param x x
	 * @param y y
	 * @return the member
	 */
	public Member add(Number x, Number y) {
		return add(new Point(x, y));
	}

	/**
	 * Get the size of the point list
	 * <p>
	 * @return the size
	 */
	public int size() {
		return points.size();
	}

	/**
	 * The list of points.
	 * <p>
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
//			sb.append(func.getName()).append("(").append(func.getArgumentName()).append(") -> ");
			sb.append(getFunctionCall().toLogString()).append(" -> ");
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
		return "Member{" + "name=" + name + '}';
	}

	// ================= CALCULAIONS ========================================
	/**
	 * Adjust the point list so we have values at the end points
	 * <p>
	 * @param start start interval
	 * @param stop  end interval
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
	 * <p>
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
	 * <p>
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

				// apply the hedge if we have one
				if(haveHedge()) {
					interpolated = getHedge().calculateValue(interpolated);
				}
				exploded.add(interpolated);
				return;
			}
		}
	}

	/**
	 * Find the max.
	 * <p>
	 * @return the max
	 */
	double findMax() {
		double max = exploded.stream().reduce(Double.MIN_VALUE, (x, y) -> x > y ? x : y);
		return max;
	}

	/**
	 * Create the normalized point set.
	 * <p>
	 * @param yMax    the overall max value
	 * @param normMax the normalized max
	 */
	void normalizeY(double yMax, int normMax) {
		exploded.forEach(y -> {
			int norm = (int) Math.round((y / yMax) * normMax);
			normalized.add(norm);
		});
	}

	/**
	 * Get the exploded values.
	 * <p>
	 * @return the exploded values
	 */
	public Collection<Double> exploded() {
		return exploded;
	}

	/**
	 * Get the normalied values.
	 * <p>
	 * @return the normalized values
	 */
	public List<Integer> normalized() {
		return normalized;
	}
}
