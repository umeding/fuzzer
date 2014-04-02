/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

/**
 * Main program.
 * <p>
 * @author uwe
 */
public class Fuzzer {

	private static final GetOpt options;

	public static boolean DEBUG = false;

	static {
		options = new GetOpt()
				.add("help", "h", false, "Some help")
				.add("debug", "d", false, "Debug (currently default)")
				.add("package", "p", true, "Java package")
				.add("outputdir", "o", true, "Output directory");
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

		for (GetOpt.Option opt : options.parseOptions(av)) {
			switch (opt.getLongName()) {
				case "help":
					printHelp();
					return;
			}

		}
		for (int i = options.getOptind(); i < av.length; i++) {
			System.out.println("--> " + av[i]);
		}
		System.out.println("--> done");
	}

	public static void main(String... av) throws Exception {
		new Fuzzer().execute(av);
	}
}
