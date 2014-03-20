/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer.java;

import com.uwemeding.fuzzer.ConditionEvaluator;
import com.uwemeding.fuzzer.Expression;
import com.uwemeding.fuzzer.FuzzerException;
import com.uwemeding.fuzzer.FuzzerOutput;
import com.uwemeding.fuzzer.Member;
import com.uwemeding.fuzzer.Node;
import static com.uwemeding.fuzzer.Node.Type.OR;
import com.uwemeding.fuzzer.Program;
import com.uwemeding.fuzzer.Rule;
import com.uwemeding.fuzzer.Variable;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * The JAVA output strategy.
 *
 * @author uwe
 */
public class JavaOutputType implements FuzzerOutput {

	private final NameMap nameMap;

	public JavaOutputType() {
		this.nameMap = new NameMap();
	}

	@Override
	public ConditionEvaluator getConditionEvaluator() {
		return new JavaConditionEvaluator();
	}

	@Override
	public void createOutput(Properties props, Program program) {
		System.out.println("Create a java program");
		try {
			nameMap.clear();
			createProgram(program);
		} catch (IOException ex) {
			throw new FuzzerException(ex);
		}
	}

	private void createProgram(Program program) throws IOException {
		Java.CLASS clazz = Java.createClass("public", program.getName());
		clazz.setPackage("com.uwemeding.fuzzer");

		Java.METHOD ctor = clazz.addCTOR("public");
		ctor.setComment("Construct a fuzzy object");

		Java.METHOD method = clazz.addMETHOD("public", "CRISP", "calculate");
		method.setComment("Calculate new values");
		for (Variable v : program.inputs()) {
			method.addArg("Number", v.getName(), v.toLogString());
		}

		// add the output fuzzy sets
		for (Variable v : program.outputs()) {
			method.addS("double[] " + v.getName() + " = new double[" + v.getTotalSteps() + "]");
		}

		// fire the rule if we have a meaningful result
		for (Rule rule : program.rules()) {
			method.addC(true, rule.getName());
			String varName = addCondition(method, rule, rule.getCondition());
			for (Variable output : rule.assignmentVariables()) {
				Member member = rule.getMember(output);
				String frv = output.getName() + "$" + member.getName();

				Java.IF fire = method.addIF("Math.abs(" + varName + ".doubleValue()) > " + program.getEpsilon());
				fire.addC(true, "assign " + output.getName() + " to " + member.getName());
				Java.FOR fout = fire.addFOR("int i = 0", "i<" + output.getTotalSteps(), "i++");
				fout.addS("Number map = (double)" + frv + "[i] / 255.0");
				fout.addS("map = rs(" + varName + ", map)");
				fout.addS(output.getName() + "[i] = Math.max(" + output.getName() + "[i], map.doubleValue())");

			}
		}

		method.addLine();
		method.addC(true, "Calculate the crisp values");
		method.addLine("CRISP crisp = new CRISP(");
		StringBuilder sb = new StringBuilder();
		int noutputs = program.outputs().size();
		for (Variable v : program.outputs()) {
			String delim = --noutputs > 0 ? "," : "";
			method.addLine("    calculateCrispValue("
					+ v.getFrom() + ", "
					+ v.getTo() + ", "
					+ v.getStep() + ", "
					+ v.getName() + ")" + delim);
		}
		method.addLine(");");
		method.addRETURN("crisp");

		addIntegerRoundingMethod(clazz);
		addReasoningMethod(clazz, program);
		addFindAssociation(clazz);
		addCalculateCrispValue(program, clazz);
		addCrispOutputs(clazz, program);

		clazz.addC(true, "======== INPUTS =========");
		for (Variable v : program.inputs()) {
			for (Member m : v.members()) {
				String varName = v.getName() + "$" + m.getName();
				String content = createNormalized(m);
				Java.VAR var = clazz.addVAR("private final", "int[]", varName, content);
				var.setComment("Variable: " + v.getName() + " Member: " + m.getName());
			}
		}
		clazz.addC(true, "======== OUTPUTS =========");
		for (Variable v : program.outputs()) {
			for (Member m : v.members()) {
				String varName = v.getName() + "$" + m.getName();
				String content = createNormalized(m);
				Java.VAR var = clazz.addVAR("private final", "int[]", varName, content);
				var.setComment("Variable: " + v.getName() + " Member: " + m.getName());
			}
		}
		Java.setBaseDirectory(new File("./src/test/java"));
		Java.createSource(clazz);
	}

	private String addCondition(Java.METHOD method, Rule rule, Node node) {

		String varName;
		switch (node.getNodeType()) {
			case IN:
				Expression in = node.cast();
				Variable var = in.getLeft().cast();
				Member member = in.getRight().cast();
				varName = nameMap.map(rule.getName() + "_" + var.getName() + "_in_" + member.getName());

//				method.addC(var.getName() + " in " + member.getName());
				callFindAssociation(method, varName, var, member);
				return varName;

			case OR:
			case AND:
				Expression comb = node.cast();
				String l = addCondition(method, rule, comb.getLeft());
				String r = addCondition(method, rule, comb.getRight());
				varName = nameMap.map(rule.getName() + "_" + l + "_" + node.getNodeType() + "_" + r);

//				method.addC(true, node.getNodeType().toString());
				String op;
				if (node.getNodeType() == OR) {
					op = "Math.max";
				} else {
					op = "Math.min";
				}
				method.addS("Number " + varName + " = " + op + "(" + l + ".doubleValue(), " + r + ".doubleValue())");
				return varName;

			default:
				throw new FuzzerException(node.getNodeType() + ": unable to process");
		}
	}

	private void addCrispOutputs(Java.CLASS clazz, Program program) {
		// inner class
		Java.CLASS crispClass = clazz.addCLASS("public static", "CRISP");
		crispClass.setComment("Crisp output values");
		for (Variable v : program.outputs()) {
			crispClass.addVAR("private final", "Number", v.getName());
		}

		Java.METHOD crispCtor = crispClass.addCTOR("private");
		crispCtor.setComment("Create crisp output values");
		for (Variable v : program.outputs()) {
			crispCtor.addArg("Number", v.getName(), v.getName() + " crisp value");
			crispCtor.addS("this." + v.getName() + " = " + v.getName());
		}

		// accessors
		for (Variable v : program.outputs()) {
			String getterName = makeGetter(v.getName());
			Java.METHOD getter = crispClass.addMETHOD("public", "Number", getterName);
			getter.setComment("Get the " + v.getName() + " crisp value");
			getter.setReturnComment("the "+v.getName()+" crisp value");
			getter.addRETURN(v.getName());
		}
	}

	private String makeGetter(String attr) {
		StringBuilder sb = new StringBuilder((attr.length() + 3));
		sb.append("get");
		sb.append(attr.substring(0, 1).toUpperCase());
		sb.append(attr.substring(1));
		return sb.toString();
	}

	/**
	 * Add the integer rounding method to the generated code.
	 *
	 * @param clazz the class wrapper
	 */
	private void addIntegerRoundingMethod(Java.CLASS clazz) {
		// round method
		Java.METHOD iround = clazz.addMETHOD("private", "int", "iround");
		iround.setComment("Round to the nearest integer");
		iround.setReturnComment("the nearest integer");
		iround.addArg("Number", "value", "value to be rounded");
		iround.addRETURN("(int)Math.round(value.doubleValue())");
	}

	/**
	 * Add the reasoning method to the generated code.
	 *
	 * @param clazz the class wrapper
	 * @param program the fuzzy program
	 * @throws FuzzerException
	 */
	private void addReasoningMethod(Java.CLASS clazz, Program program) throws FuzzerException {
		// reasoning strategy
		Java.METHOD rs = clazz.addMETHOD("private", "Number", "rs");
		rs.setComment("Reasoning strategy is " + program.getReasoningStrategy().getName());
		rs.setReturnComment("the reasoned value");
		rs.addArg("Number", "a", "strength value");
		rs.addArg("Number", "b", "mapped value");
		switch (program.getReasoningStrategy()) {
			case MAXDOT:
				rs.addRETURN("a.doubleValue() * b.doubleValue()");
				break;
			case MAXMIN:
				rs.addRETURN("a.doubleValue() < b.doubleValue() ? a : b");
				break;
			default:
				throw new FuzzerException(program.getReasoningStrategy() + ": not implemented");
		}
	}

	/**
	 * Calculate the crisp output value.
	 *
	 * @param method the calling method
	 * @param variable the output variable
	 */
	private void addCalculateCrispValue(Program program, Java.CLASS clazz) {

		Java.METHOD calc = clazz.addMETHOD("private", "Number", "calculateCrispValue");
		calc.setComment("Calculate the crisp value");
		calc.setReturnComment("the crisp value");
		calc.addArg("Number", "from", "Start interval");
		calc.addArg("Number", "to", "End interval");
		calc.addArg("Number", "step", "Interval step");
		calc.addArg("double[]", "fuzzy", "Fuzzy value");

		calc.addS("double area = 0.0");
		calc.addS("double moment = 0.0");

		Java.FOR fout = calc.addFOR("int i = 0", "i < fuzzy.length", "i++");
		fout.addS("double normalized = from.doubleValue() + (step.doubleValue() * i)");
		fout.addS("area += fuzzy[i]");
		fout.addS("moment += fuzzy[i] * normalized");
		calc.addS("double crisp = Math.abs(area) < " + program.getEpsilon() + " ? "
				+ "to.doubleValue() + step.doubleValue() : moment / area");
		calc.addRETURN("Math.abs(crisp) > "+program.getEpsilon()+ " ? crisp : 0.0");

	}

	/**
	 * Call the find association method.
	 *
	 * @param method the invoking method
	 * @param result the result variable
	 * @param variable the input variable
	 * @param member the member
	 */
	private void callFindAssociation(Java.METHOD method, String result, Variable variable, Member member) {

		String setName = variable.getName() + "$" + member.getName();
		method.addS("Number " + result + " = findAssociation("
				+ variable.getName() + ", "
				+ variable.getFrom() + ", "
				+ variable.getTo() + ", "
				+ variable.getStep() + ", "
				+ setName + ")");
	}

	/**
	 * Add the method to calculate the association of an input variable in a
	 * fuzzy range.
	 *
	 * @param clazz the class wrapper
	 */
	private void addFindAssociation(Java.CLASS clazz) {
		Java.METHOD assoc = clazz.addMETHOD("private", "Number", "findAssociation");
		assoc.setComment("Determine the association of a value into a fuzzy range");
		assoc.setReturnComment("the association");
		assoc.addArg("Number", "value", "value to check");
		assoc.addArg("Number", "from", "fuzzy range start");
		assoc.addArg("Number", "to", "fuzzy range end");
		assoc.addArg("Number", "step", "fuzzy range step");
		assoc.addArg("int[]", "frv", "fuzzy range values");
		Java.IF vlimit = assoc.addIF("value.doubleValue() < from.doubleValue()");
		vlimit.addS("value = from");
		vlimit.addELSEIF("value.doubleValue() > to.doubleValue()")
				.addS("value = to");

		assoc.addS("int index = iround((value.doubleValue() - from.doubleValue()) / step.doubleValue())");

		assoc.addIF("index > frv.length").addS("index = frv.length - 1");
		assoc.addIF("index < 0").addS("index = 0");

		assoc.addS("Number mapped = frv[index]");
		assoc.addRETURN("mapped.doubleValue() / 255.0");
	}

	private String createNormalized(Member m) {
		StringBuilder sb = new StringBuilder();

		int count = 0;
		String delim = "";
		sb.append("{");
		for (Integer i : m.normalized()) {
			if (count++ % 10 == 0) {
				sb.append(delim).append("\n        ");
			} else {
				sb.append(delim);
			}
			sb.append(String.format(" %3d", i));
			delim = ",";
		}
		sb.append("\n    }");
		return sb.toString();
	}

}
