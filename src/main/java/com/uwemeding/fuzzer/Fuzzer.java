/*
 * Copyright (c) 2014 Meding Software Technik - All Rights Reserved.
 */
package com.uwemeding.fuzzer;

import com.uwemeding.fuzzer.java.JavaOutputType;
import com.uwemeding.fuzzer.parser.FuzzerParser;
import java.io.FileInputStream;

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

		String outputdir = "./";
		String packageName = null;
		boolean packageNameDefined = false;
		for (GetOpt.Option opt : options.parseOptions(av)) {
			switch (opt.getLongName()) {

				case "help":
					printHelp();
					return;

				case "outputdir":
					outputdir = opt.getValue();
					break;

				case "package":
					packageName = opt.getValue();
					packageNameDefined = true;
					break;
			}

		}
		// now loop through the input files
		for (int i = options.getOptind(); i < av.length; i++) {
			FuzzerParser parser = new FuzzerParser();
			FuzzerOutputContext context = new FuzzerOutputContext(new JavaOutputType());

			try (FileInputStream fp = new FileInputStream(av[i])) {
				Program program = parser.parse(fp);
				if (packageNameDefined) {
					program.setPackageName(packageName);
				}

				// compile the program
				ProgramEvaluator eval = new ProgramEvaluator(program);
				eval.compileProgram();

				context.create(outputdir, program);
			}
		}
		System.out.println("--> done");
	}

	public static void main(String... av) throws Exception {
		new Fuzzer().execute(av);
	}
}
