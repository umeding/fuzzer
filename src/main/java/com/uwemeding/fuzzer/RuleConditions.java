/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import static com.uwemeding.fuzzer.Node.Type.AND;
import static com.uwemeding.fuzzer.Node.Type.IN;
import static com.uwemeding.fuzzer.Node.Type.MEMBER;
import static com.uwemeding.fuzzer.Node.Type.OR;
import java.util.StringJoiner;

/**
 * Conditions helper.
 * <p>
 * @author uwe
 */
public class RuleConditions {

	/**
	 * Create an "IN" condition.
	 * <p>
	 * @param var  is the variable
	 * @param cond is either a member or a hedging condition
	 * @return the in condition
	 */
	public static Node createInCondition(Variable var, Node cond) {
		checkNode(cond, MEMBER);
		// make sure the member is part of the variable
		Member member;
		switch (cond.getNodeType()) {

			case MEMBER:
				member = cond.cast();
				break;

			default:
				throw new FuzzerException(cond + ": unexpected IN condition");
		}
		if (!var.haveMember(member.getName())) {
			throw new FuzzerException(member.getName() + ": not a member of " + var.getName());
		}
		return new Condition(Node.Type.IN, var, cond);
	}

	/**
	 * Create an "AND" condition.
	 * <p>
	 * @param left  left side of condition
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
	 * <p>
	 * @param left  left side of condition
	 * @param right right side of condition
	 * @return the "OR" condition
	 */
	public static Node createOrCondition(Node left, Node right) {
		checkNode(left, IN, AND, OR);
		checkNode(right, IN, AND, OR);
		return new Condition(Node.Type.OR, left, right);
	}

	/**
	 * Check a node for validity.
	 * <p>
	 * @param node       the node
	 * @param validTypes expected types
	 */
	public static void checkNode(Node node, Node.Type... validTypes) {
		for (Node.Type type : validTypes) {
			if (node.getNodeType() == type) {
				return;
			}
		}
		StringJoiner sj = new StringJoiner(", ");
		for (Node.Type type : validTypes) {
			sj.add(type.toString());
		}
		String nodeName = node.getNodeType().name();
		if (node instanceof NameBearer) {
			nodeName = ((NameBearer) node).getName();
		}
		throw new FuzzerException(nodeName + ": invalid, must be one of " + sj.toString());
	}

	private static class Condition extends RuleExpression {

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
			return "Condition{" + "nodeType=" + nodeType + ", left=" + left + ", right=" + right + '}';
		}

	}
}
