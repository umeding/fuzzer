/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import com.uwemeding.fuzzer.RuleExpression;
import com.uwemeding.fuzzer.Member;
import com.uwemeding.fuzzer.Node;
import com.uwemeding.fuzzer.Program;
import com.uwemeding.fuzzer.Rule;

/**
 *
 * @author uwe
 */
public class ProgramEvaluator {

	private final Program program;

	public ProgramEvaluator(Program program) {
		this.program = program;
	}

	/**
	 * Compile the program
	 */
	public void compileProgram() {
		program.inputs().forEach(var -> var.calculateFuzzySpace());
		program.outputs().forEach(var -> var.calculateFuzzySpace());

		program.rules().forEach(rule -> {
			updateMemberReferenceCount(rule.getCondition());
			rule.assignmentMembers().forEach(member -> member.incrReferenceCount());
		});
	}

	/*
	 * update the member reference counts
	 */
	private void updateMemberReferenceCount(Node node) {
		switch (node.getNodeType()) {
			case AND:
			case IN:
			case OR:
				RuleExpression expr = node.cast();
				updateMemberReferenceCount(expr.getLeft());
				updateMemberReferenceCount(expr.getRight());
				break;

			case VARIABLE:
				break;

			case MEMBER:
				Member member = node.cast();
				member.incrReferenceCount();
				break;
		}
	}

}
