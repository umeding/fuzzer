/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A fuzzy logic program.
 *
 * @author uwe
 */
public class Program {

	private final String name;

	private final Map<String, Hedge> hedges;

	private final Map<String, Function> functions;

	private final Map<String, Variable> inputs;
	private final Map<String, Variable> outputs;

	public Program(String name) {
		this.name = name;
		this.hedges = new HashMap<>();
		this.functions = new HashMap<>();
		this.inputs = new HashMap<>();
		this.outputs = new HashMap<>();
	}

	/**
	 * Get the hedges.
	 *
	 * @return the hedges
	 */
	public Collection<Hedge> hedges() {
		return hedges.values();
	}

	/**
	 * Get a hedge by name.
	 *
	 * @param hedgeName the hedge name
	 * @return the hedge
	 */
	public Hedge getHedge(String hedgeName) {
		return getItem(hedgeName, hedges, "hedge");
	}

	/**
	 * Add a hedge to this program.
	 *
	 * @param hedge the hedge
	 * @return the hedge
	 */
	public Hedge addHedge(Hedge hedge) {
		addItem(hedge, hedges, "hedges");
		return hedge;
	}

	/**
	 * Add a hedge by name.
	 *
	 * @param name name of the hedge
	 * @param arg argument
	 * @param expression the expression
	 * @return the hedge
	 */
	public Hedge addHedge(String name, String arg, String expression) {
		return addHedge(new Hedge(name, arg, expression));
	}

	/**
	 * Get the functions.
	 *
	 * @return the functions
	 */
	public Collection<Function> functions() {
		return functions.values();
	}

	/**
	 * Get a function be name.
	 *
	 * @param functionName the function name
	 * @return the function
	 */
	public Function getFunction(String functionName) {
		return getItem(functionName, functions, "function");
	}

	/**
	 * Add a function to this program.
	 *
	 * @param function the function
	 * @return the function
	 */
	public Function addFunction(Function function) {
		addItem(function, functions, "functions");
		return function;
	}

	/**
	 * Add a function with a name.
	 *
	 * @param name the name of the function
	 * @return the function
	 */
	public Function addFunction(String name) {
		return addFunction(new Function(name));
	}

	/**
	 * Get the inputs.
	 *
	 * @return the inputs
	 */
	public Collection<Variable> inputs() {
		return inputs.values();
	}

	/**
	 * Get an input by name.
	 *
	 * @param inputName the input name
	 * @return the input
	 */
	public Variable getInput(String inputName) {
		return getItem(inputName, inputs, "input");
	}

	/**
	 * Add an input variable to this program.
	 *
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
	 *
	 * @param <T> the type
	 * @param name the variable name
	 * @param from start of interval
	 * @param to end of interval
	 * @param step the step
	 * @return the variable
	 */
	public <T extends Number> Variable addInput(String name, T from, T to, T step) {
		return addInput(new Variable(name, from, to, step));
	}

	/**
	 * Get the outputs.
	 *
	 * @return the outputs
	 */
	public Collection<Variable> outputs() {
		return outputs.values();
	}

	/**
	 * Get an output by name.
	 *
	 * @param outputName the output name
	 * @return the output
	 */
	public Variable getOutput(String outputName) {
		return getItem(outputName, outputs, "output");
	}

	/**
	 * Add an output variable to this program.
	 *
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
	 *
	 * @param <T> the type
	 * @param name the variable name
	 * @param from start of interval
	 * @param to end of interval
	 * @param step the step
	 * @return the variable
	 */
	public <T extends Number> Variable addOutput(String name, T from, T to, T step) {
		return addOutput(new Variable(name, from, to, step));
	}

	/**
	 * Check if a variable is unique.
	 *
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
	 *
	 * @param <T> the map type
	 * @param itemName the item name
	 * @param items the items
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
	 *
	 * @param <T> the item type
	 * @param item the item
	 * @param items the item map
	 * @param typeName the item type name string
	 */
	private <T extends NameBearer> void addItem(T item, Map<String, T> items, String typeName) {
		String itemName = item.getName();
		if (items.containsKey(itemName)) {
			throw new FuzzerException(name + ": '" + itemName + "' already present in " + typeName);
		}
		items.put(itemName, item);
	}

	/**
	 * Dump the content of the program.
	 *
	 * @param fp the printer
	 */
	public void dump(PrintStream fp) {
		fp.println("Program " + name);

		String dashes = "----------------------------------------------------------------------";
		String fmt = "%10s : %15s %s%n";

		// header
		fp.println(dashes);
		fp.format(fmt, "Name", "Type", "Content");
		fp.println(dashes);
		fp.println();

		Set<String> names = new TreeSet<>(hedges.keySet());
		for (String name : names) {
			Hedge hedge = hedges.get(name);
			fp.format(fmt, name, "<hedge>", hedge.getExpression());
		}

		names = new TreeSet<>(functions.keySet());
		for (String name : names) {
			Function function = functions.get(name);
			fp.format(fmt, name, "<func>", function.toLogString());
		}

		names = new TreeSet<>(inputs.keySet());
		for (String name : names) {
			Variable var = inputs.get(name);
			fp.format(fmt, name, "<input var>", var.toLogString());
		}

		names = new TreeSet<>(outputs.keySet());
		for (String name : names) {
			Variable var = outputs.get(name);
			fp.format(fmt, name, "<output var>", var.toLogString());
		}

		// we are done
		fp.println();
		fp.println(dashes);
		fp.println();
	}

}
