/*
 * Copyright (c) 2014 Meding Software Technik -- All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Manage command line options.
 *
 * @author uwe
 */
public class GetOpt {

	private final Map<String, Definition> longOpts;
	private final Map<String, Definition> shortOpts;
	private final List<Definition> definitions;
	private int optind;

	public GetOpt() {
		this.longOpts = new HashMap<>();
		this.shortOpts = new HashMap<>();
		this.definitions = new ArrayList<>();
		this.optind = 0;
	}

	public GetOpt add(String longName, String shortName, boolean hasArg, String helpText) {
		add(new Definition(longName, shortName, hasArg, helpText));
		return this;
	}

	public void add(Definition def) {
		definitions.add(def);
		if (longOpts.containsKey(def.getLongName())) {
			throw new IllegalArgumentException("Option '" + def.getLongName() + "' already defined");
		}
		if (shortOpts.containsKey(def.getShortName())) {
			throw new IllegalArgumentException("Option '" + def.getShortName() + "' already defined");
		}
		longOpts.put(def.getLongName(), def);
		shortOpts.put(def.getShortName(), def);
	}

	public Iterable<Definition> definitions() {
		return definitions::iterator;
	}

	public int getOptind() {
		return optind;
	}

	public Iterable<Option> parse(final String... av) {
		optind = av.length;
		final GetOpt go = this;
		return () -> new OptionIterator(go, av);
	}

	public static class Definition {

		private final String longName;
		private final String shortName;
		private final boolean hasArg;
		private final String helpText;

		public Definition(String longName, String shortName, boolean hasArg, String helpText) {
			this.longName = longName;
			this.shortName = shortName;
			this.hasArg = hasArg;
			this.helpText = helpText;
		}

		public boolean hasArg() {
			return hasArg;
		}

		public String getLongName() {
			return longName;
		}

		public String getShortName() {
			return shortName;
		}

		public String getHelpText() {
			return helpText;
		}

		@Override
		public String toString() {
			return hasArg ? "-" + shortName + " ARG | --" + longName + "=ARG" : "-" + shortName + "     | --" + longName;
		}
	}

	public static class Option {

		private final Definition arg;
		private final String value;

		public Option(Definition arg, String value) {
			this.arg = arg;
			this.value = value;
		}

		public String getLongName() {
			return arg.getLongName();
		}

		public String getShortName() {
			return arg.getShortName();
		}

		public String getValue() {
			if (value == null) {
				throw new IllegalArgumentException("Option " + arg + " has no value");
			} else {
				return value;
			}
		}

		@Override
		public String toString() {
			return value == null ? arg.toString() : arg.toString() + ": " + value;
		}
	}

	private static class OptionIterator implements Iterator<Option> {

		private final GetOpt getOpt;
		private final String[] av;
		private int pos;

		public OptionIterator(GetOpt getOpt, String... av) {
			this.getOpt = getOpt;
			this.av = av;
			this.pos = 0;
		}

		@Override
		public boolean hasNext() {
			getOpt.optind = pos;
			return pos < av.length && av[pos].startsWith("-");
		}

		@Override
		public Option next() {
			Option opt;
			String arg = av[pos++];
			if (arg.startsWith("--")) {
				int sepPos = arg.indexOf("=");
				String key;
				String value;
				if (sepPos > 0) {
					key = arg.substring(2, sepPos);
					value = arg.substring(sepPos + 1);
				} else {
					key = arg.substring(2);
					value = null;
				}

				Definition def = getOpt.longOpts.get(key);
				if (def == null) {
					throw new IllegalArgumentException("Unexpected argument " + arg);
				} else {
					if (def.hasArg()) {
						if (value == null) {
							throw new IllegalArgumentException("Argument expected for " + arg);
						}

					} else {
						if (value != null) {
							throw new IllegalArgumentException("Unexpected argument " + arg);
						}
					}
				}

				opt = new Option(def, value);
			} else if (arg.startsWith("-")) {
				String key = arg.substring(1);
				String value = null;
				Definition def = getOpt.shortOpts.get(key);
				if (def == null) {
					throw new IllegalArgumentException("Unexpected argument " + arg);
				} else {
					if (def.hasArg()) {
						if (pos < av.length) {
							if (av[pos].startsWith("-")) {
								throw new IllegalArgumentException("Argument expected for " + arg);
							}
							value = av[pos++];
						} else {
							throw new IllegalArgumentException("Argument expected for " + arg);
						}
					}
				}
				opt = new Option(def, value);
			} else {
				throw new IllegalArgumentException("Unexpected argument " + arg);
			}
			return opt;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}
}
