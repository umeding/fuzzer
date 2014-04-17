/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A fuzzy logic program.
 * <p>
 * @author uwe
 */
public class Program {

	private final static Double EPSILON = 0.001;

	private final String name;

	private final Map<String, Hedge> hedges;

	private final Map<String, Function> functions;

	private final Map<String, Variable> inputs;
	private final Map<String, Variable> outputs;

	private final Map<String, Rule> rules;

	private String packageName;
	private ReasoningStrategy reasoningStrategy;
	private Number epsilon; // values less are considered 0

	public Program(String name) {
		if (!IdentifierHelper.isValid(name)) {
			throw new FuzzerException(name + ": identifier illegal");
		}
		this.name = name;
		this.hedges = new HashMap<>();
		this.functions = new HashMap<>();
		this.inputs = new HashMap<>();
		this.outputs = new HashMap<>();
		this.rules = new HashMap<>();

		this.reasoningStrategy = ReasoningStrategy.getDefault();
		this.epsilon = EPSILON;
	}

	/**
	 * Get the program name.
	 * <p>
	 * @return the program name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the selected reasoning strategy.
	 * <p>
	 * @return the selected reasoning strategy
	 */
	public ReasoningStrategy getReasoningStrategy() {
		return reasoningStrategy;
	}

	/**
	 * Set the reasoning strategy.
	 * <p>
	 * @param reasoningStrategy the reasoning strategy
	 */
	public void setReasoningStrategy(ReasoningStrategy reasoningStrategy) {
		this.reasoningStrategy = reasoningStrategy;
	}

	/**
	 * Get the package name.
	 * <p>
	 * @return the package name
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Set the package name.
	 * <p>
	 * @param packageName the package name
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * Get epsilon.
	 * <p>
	 * @return epsilon
	 */
	public Number getEpsilon() {
		return epsilon;
	}

	/**
	 * Set epsilon.
	 * <p>
	 * @param epsilon epsilon
	 */
	public void setEpsilon(Number epsilon) {
		this.epsilon = epsilon;
	}

	/**
	 * Add a rule to this program.
	 * <p>
	 * @param name      is the rule name
	 * @param condition is the rule condition
	 * @return the rule
	 */
	public Rule addRule(String name, Node condition) {
		Rule rule = new Rule(name, condition);
		rules.put(name, rule);
		return rule;
	}

	/**
	 * Get the rules.
	 * <p>
	 * @return the rules
	 */
	public Collection<Rule> rules() {
		return rules.values();
	}

	/**
	 * The rule names.
	 * <p>
	 * @return the rule names
	 */
	public Collection<String> ruleNames() {
		return rules.keySet();
	}

	/**
	 * Get a rule by name.
	 * <p>
	 * @param name the rule name
	 * @return the rule
	 */
	public Rule getRule(String name) {
		Rule rule = rules.get(name);
		if (rule == null) {
			throw new FuzzerException(name + ": rule not found");
		}
		return rule;
	}

	/**
	 * Get the hedges.
	 * <p>
	 * @return the hedges
	 */
	public Collection<Hedge> hedges() {
		return hedges.values();
	}

	/**
	 * Get a hedge by name.
	 * <p>
	 * @param hedgeName the hedge name
	 * @return the hedge
	 */
	public Hedge getHedge(String hedgeName) {
		return getItem(hedgeName, hedges, "hedge");
	}

	/**
	 * Add a hedge to this program.
	 * <p>
	 * @param hedge the hedge
	 * @return the hedge
	 */
	public Hedge addHedge(Hedge hedge) {
		addItem(hedge, hedges, "hedges");
		return hedge;
	}

	/**
	 * Add a hedge by name.
	 * <p>
	 * @param name       name of the hedge
	 * @param arg        argument
	 * @param expression the expression
	 * @return the hedge
	 */
	public Hedge addHedge(String name, String arg, String expression) {
		return addHedge(new Hedge(name, arg, expression));
	}

	/**
	 * Get the functions.
	 * <p>
	 * @return the functions
	 */
	public Collection<Function> functions() {
		return functions.values();
	}

	/**
	 * Get a function be name.
	 * <p>
	 * @param functionName the function name
	 * @return the function
	 */
	public Function getFunction(String functionName) {
		return getItem(functionName, functions, "function");
	}

	/**
	 * Add a function to this program.
	 * <p>
	 * @param <T>      the function type
	 * @param function the function
	 * @return the function
	 */
	public <T extends Function> T addFunction(T function) {
		addItem(function, functions, "functions");
		return function;
	}

	/**
	 * Add an external function.
	 * <p>
	 * @param name         function name
	 * @param argumentName the argument name
	 * @return the function
	 */
	public ExternalFunction addExternalFunction(String name, String argumentName) {
		return addFunction(new ExternalFunction(name, argumentName));
	}

	/**
	 * Add a piecewise function.
	 * <p>
	 * @param name         is the name of the function
	 * @param argumentName argument name
	 * @return the function
	 */
	public PiecewiseFunction addPiecewiseFunction(String name, String argumentName) {
		return addFunction(new PiecewiseFunction(name, argumentName));
	}

	/**
	 * Get the inputs.
	 * <p>
	 * @return the inputs
	 */
	public Collection<Variable> inputs() {
		return inputs.values();
	}

	/**
	 * Get an input by name.
	 * <p>
	 * @param inputName the input name
	 * @return the input
	 */
	public Variable getInput(String inputName) {
		return getItem(inputName, inputs, "input");
	}

	/**
	 * Add an input variable to this program.
	 * <p>
	 * @param var the variable
	 * @return the input variable
	 */
	public Variable addInput(Variable var) {
		checkUnique(var);
		addItem(var, inputs, "inputs");
		return var;
	}

	/**
	 * Add an input variable to this program.
	 * <p>
	 * @param <T>  the type
	 * @param name the variable name
	 * @param from start of interval
	 * @param to   end of interval
	 * @param step the step
	 * @return the variable
	 */
	public <T extends Number> Variable addInput(String name, T from, T to, T step) {
		return addInput(new Variable(name, from, to, step));
	}

	/**
	 * Get the outputs.
	 * <p>
	 * @return the outputs
	 */
	public Collection<Variable> outputs() {
		return outputs.values();
	}

	/**
	 * Get an output by name.
	 * <p>
	 * @param outputName the output name
	 * @return the output
	 */
	public Variable getOutput(String outputName) {
		return getItem(outputName, outputs, "output");
	}

	/**
	 * Add an output variable to this program.
	 * <p>
	 * @param var the variable
	 * @return the output variable
	 */
	public Variable addOutput(Variable var) {
		checkUnique(var);
		addItem(var, outputs, "outputs");
		return var;
	}

	/**
	 * Add an output variable to this program.
	 * <p>
	 * @param <T>  the type
	 * @param name the variable name
	 * @param from start of interval
	 * @param to   end of interval
	 * @param step the step
	 * @return the variable
	 */
	public <T extends Number> Variable addOutput(String name, T from, T to, T step) {
		return addOutput(new Variable(name, from, to, step));
	}

	/**
	 * Check if a variable is unique.
	 * <p>
	 * @param var the variable
	 */
	private void checkUnique(Variable var) {
		if (inputs.containsKey(var.getName())) {
			throw new FuzzerException(var.getName() + ": already defined");
		}
		if (outputs.containsKey(var.getName())) {
			throw new FuzzerException(var.getName() + ": already defined");
		}
	}

	/**
	 * Get an item from a map.
	 * <p>
	 * @param <T>      the map type
	 * @param itemName the item name
	 * @param items    the items
	 * @param typeName the item type name
	 * @return the item
	 */
	private <T> T getItem(String itemName, Map<String, T> items, String typeName) {
		T item = items.get(itemName);
		if (item == null) {
			throw new FuzzerException(name + ": " + typeName + " '" + itemName + "' not found");
		}
		return item;
	}

	/**
	 * Add an item to a map.
	 * <p>
	 * @param <T>      the item type
	 * @param item     the item
	 * @param items    the item map
	 * @param typeName the item type name string
	 */
	private <T extends NameBearer> void addItem(T item, Map<String, T> items, String typeName) {
		String itemName = item.getName();
		if (items.containsKey(itemName)) {
			throw new FuzzerException(name + ": '" + itemName + "' already present in " + typeName);
		}
		items.put(itemName, item);
	}

	private static final String FMT = "  %-15s : %15s %s%n";

	/**
	 * Dump the content of the program.
	 * <p>
	 * @param fp the printer
	 */
	public void dump(PrintStream fp) {
		fp.println("Program " + name);

		String dashes = "----------------------------------------------------------------------";

		// header
		fp.println(dashes);
		fp.format(FMT, "Name", "Type", "Content");
		fp.println(dashes);
		fp.println();

		Set<String> names = new TreeSet<>(hedges.keySet());
		names.forEach(name
				-> fp.format(FMT, name, "<hedge>", hedges.get(name).getExpressionString())
		);

		names = new TreeSet<>(functions.keySet());
		names.forEach(name
				-> fp.format(FMT, name, "<func def>", functions.get(name).toLogString())
		);

		names = new TreeSet<>(inputs.keySet());
		names.forEach(name -> dump(fp, inputs.get(name), "input"));

		names = new TreeSet<>(outputs.keySet());
		names.forEach(name -> dump(fp, outputs.get(name), "output"));

		names = new TreeSet<>(rules.keySet());
		names.forEach(name -> fp.format(FMT, name, "<rule>", rules.get(name)));

		// we are done
		fp.println();
		fp.println(dashes);
		fp.println();
	}

	private void dump(PrintStream fp, Variable var, String varType) {
		fp.format(FMT, var.getName(), "<" + varType + " var>", var.toLogString());
		if (var.members().isEmpty()) {
			return;
		}

		var.members().forEach(member
				-> fp.format(FMT, var.getName() + "#" + member.getName(), "<member>", member.toLogString())
		);
	}
}
