/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import static com.uwemeding.fuzzer.Node.Type.AND;
import static com.uwemeding.fuzzer.Node.Type.HEDGING;
import static com.uwemeding.fuzzer.Node.Type.IN;
import static com.uwemeding.fuzzer.Node.Type.MEMBER;
import static com.uwemeding.fuzzer.Node.Type.OR;

/**
 * Conditions helper.
 *
 * @author uwe
 */
public class Conditions {

	/**
	 * Create an "IN" condition.
	 *
	 * @param var is the variable
	 * @param cond is either a member or a hedging condition
	 * @return the in condition
	 */
	public static Node createInCondition(Variable var, Node cond) {
		checkNode(cond, MEMBER, HEDGING);
		return new Condition(Node.Type.IN, var, cond);
	}

	/**
	 * Create hedging condition
	 *
	 * @param hedge the hedge
	 * @param member the member
	 * @return the hedging node
	 */
	public static Node createHedgingCondition(Hedge hedge, Member member) {
		return new Condition(Node.Type.HEDGING, hedge, member);
	}

	/**
	 * Create an "AND" condition.
	 *
	 * @param left left side of condition
	 * @param right right side of condition
	 * @return the "and" condition
	 */
	public static Node createAndCondition(Node left, Node right) {
		checkNode(left, IN, AND, OR);
		checkNode(right, IN, AND, OR);
		return new Condition(Node.Type.AND, left, right);
	}

	/**
	 * Create an "OR" condition.
	 *
	 * @param left left side of condition
	 * @param right right side of condition
	 * @return the "OR" condition
	 */
	public static Node createOrCondition(Node left, Node right) {
		checkNode(left, IN, AND, OR);
		checkNode(right, IN, AND, OR);
		return new Condition(Node.Type.IN, left, right);
	}

	/**
	 * Check a node for validity.
	 *
	 * @param node the node
	 * @param validTypes expected types
	 */
	public static void checkNode(Node node, Node.Type... validTypes) {
		for (Node.Type type : validTypes) {
			if (node.getNodeType() == type) {
				return;
			}
		}
		StringBuilder sb = new StringBuilder();
		String delim = "";
		for (Node.Type type : validTypes) {
			sb.append(delim).append(type);
			delim = ", ";
		}
		String nodeName = node.getNodeType().name();
		if (node instanceof NameBearer) {
			nodeName = ((NameBearer) node).getName();
		}
		throw new FuzzerException(nodeName + ": invalid, must be one of " + sb.toString());
	}

	private static class Condition extends Expression {

		private final Node.Type nodeType;
		private final Node left;
		private final Node right;

		protected Condition(Node.Type type, Node left, Node right) {
			this.nodeType = type;
			this.left = left;
			this.right = right;
		}

		@Override
		public Type getNodeType() {
			return nodeType;
		}

		@Override
		public Node getLeft() {
			return left;
		}

		@Override
		public Node getRight() {
			return right;
		}

		private String nodeName(Node node) {
			if (node == null) {
				return "null";
			}
			String nodeName = node.getNodeType().name();
			if (node instanceof NameBearer) {
				nodeName = ((NameBearer) node).getName();
			}
			return nodeName;

		}

		@Override
		public String toString() {
			String leftName = nodeName(left);
			String rightName = nodeName(right);
			return "Condition{" + "nodeType=" + nodeType + ", left=" + leftName + ", right=" + rightName + '}';
		}

	}
}
