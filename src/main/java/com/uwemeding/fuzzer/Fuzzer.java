/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

/**
 * Main program.
 *
 * @author uwe
 */
public class Fuzzer {

	private static final GetOpt options;

	public static boolean DEBUG = false;

	static {
		options = new GetOpt()
				.add("help", "h", false, "Some help")
				.add("debug", "d", false, "Debug (currently default)")
				.add("java", "j", false, "Java output")
				.add("print", "p", true, "print");
	}

	private static void printHelp() {
		printHelp(false);
	}

	private static void printHelp(boolean errorExit) {
		System.out.println("Usage:");
		System.out.println();
		System.out.println("$ java -jar fuzzer-1.0-all.jar [OPTIONS]");
		System.out.println();
		System.out.println("Options:");
		for (GetOpt.Definition def : options.definitions()) {
			System.out.printf("    %-30s %s%n", def.toString(), def.getHelpText());
		}
		System.exit(errorExit ? 1 : 0);
	}

	private void execute(String... av) throws Exception {

		for (GetOpt.Option opt : options.parse(av)) {

			switch (opt.getLongName()) {
				case "help":
					printHelp();
					return;

			}

		}
	}

	public static void main(String... av) throws Exception {
		new Fuzzer().execute(av);
	}
}
