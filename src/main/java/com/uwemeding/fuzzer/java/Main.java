package com.uwemeding.fuzzer.java;

import java.io.*;

public class Main {

	public static void main(String[] av) throws Exception {
		new Main().execute();
	}

	private void execute() throws Exception {
		Java.CLASS clazz = Java.createClass("public", "laber");
		clazz.setPackage("com.foo.something.else");
		clazz.setComment("The function of this class is to show some stuff. "
				+ "It implements nuthing, and doesn't probably compile at all");
		clazz.setAuthor("<a href=\"mailto:uwe@foo.com\">Uwe B. Meding</a>");
		clazz.setVersion("2.3");
		clazz.setExtends("Extender");
		clazz.addIMPL("schlabber");
		clazz.addIMPL("blabber");
		clazz.addIMPORT("com.foo.util");
		clazz.addIMPORT("com.foo.x");
		clazz.addIMPORT("com.foo.abc");
		clazz.addIMPORT("com.foo.corba.SomeException");

		Java.METHOD ctor = clazz.addCTOR("protected");

		Java.VAR v;
		v = clazz.addVAR("private", "String", "name");
		v.setComment("A variable name with a really long comment to see how the wrapping works. "
				+ "More stuff to test that stuff. Even more shit");
		v = clazz.addVAR("private", "String", "laber");
		v.setComment("A private variable");

		Java.METHOD m = clazz.addMETHOD("private void", "schnurz");
		m.setComment("Schnurz piep egal. Na sowas da wird ja garnichts gemacht."
				+ "Und noch mehr Kommentare und scheiss um den Puffer aufzufuellen.");
		m.addArg("String", "name");
		m.addArg("int", "number", "eine Zahl");
		m.addTHROWS("SomeException");
		m.addTHROWS("IOException", "when some IO shit happens");

		Java.TRY t = m.addTRY();
		t.addS("trying stuff");
		Java.IF ti = t.addIF("x < 1");
		ti.addS("asdfasf adsfsa");
		ti.addS("adfa dsfasdf ");
		Java.ELSE tie = ti.addELSEIF("hahaha");
		tie.addS("humpf");
		tie.addS("bumpf");
		tie.addS("schlumpf");

		t.addS("trying more stuff");
		Java.CATCH ctch = t.addCATCH("IOException", "ioe");
		ctch.addS("some stuff here");
		ctch = t.addCATCH("SomeException", "ue");
		ctch.addS("some ucp stuff  here");
		Java.CATCH fin = t.addFINALLY();
		fin.addS("finally stuff");
		fin.addC("hehe");

		m.addLABEL("schlumpf");
		Java.FOR f = m.addFOR("int i", "i<10", "i++");
		Java.IF i = f.addIF("i > 2");
		Java.WHILE w = i.addWHILE("true");
		w.addS("yo ho");
		i.addS("some = stuff");
		i.addC("Some comment goes here");
		i.addC("Some more comment goes here that is really long and spans "
				+ "more stuff ona another line\n\n"
				+ "asdf asdgf asdg asg asdgf adsg asgd asgda dsf asdf ag asd gadsgf agfa gs gf ahg");
		i.addS("some = more stuff");
		Java.ELSE e = i.addELSEIF("expr");
		e.addVAR(null, "some", "crap");
		e = i.addELSE();
		e.addVAR("some", "crap", "hehe");
		e.addVAR("some", "crap", "hehe");

		f.addFOR(null, null, null);
		f.addRETURN("stuff");
		Java.DOWHILE dw = f.addDOWHILE("some condition");
		dw.addS("shtuff");
		dw.addS("System.out.println(\"in a loop\")");
		Java.SWITCH sw = dw.addSWITCH("someValue");
		Java.CASE c;
		c = sw.addCASE("42");
		c.addS("x = y");
		c.addS("callAMethod(x, y)");
		c.setFallThru(true);

		c = sw.addCASE("43");
		c.addS("x = y + 1");
		c.addS("callBMethod(x, y)");
		c.addS("goto schlumpf");
		c.addLABEL("scheisse");

		c = sw.addDEFAULT();
		c.addS("x = y + 1");
		c.addS(null);
		c.addS("callBMethod(x, y)");

		Java.CLASS c2 = clazz.addCLASS("public static", "InternalClass");
		m = c2.addMETHOD("public void", "someMethod");
		m.setComment("Empty method");

		Java.setBaseDirectory(new File("phuqd"));
		Java.createSource(clazz);

		Java.INTERFACE itf = Java.createInterface("public", "lall");
		itf.setPackage("com.foo.something.else");
		itf.setComment("The function of this interface is to show some stuff. "
				+ "It implements nuthing, and doesn't probably compile at all");
		itf.setAuthor("<a href=\"mailto:uwe@foo.com\">Uwe B. Meding</a>");
		itf.setVersion("2.3");
		itf.addEXTENDS("hui");
		itf.addEXTENDS("del");
		itf.addIMPORT("com.foo.util");
		itf.addIMPORT("com.foo.x");
		itf.addIMPORT("com.foo.abc");
		itf.addIMPORT("com.foo.corba.SomeException");
		Java.METHOD im = itf.addMETHOD("String", "getSomeString");

		im = itf.addMETHOD("String", "getSomeOtherString");
		im.addTHROWS("SomeException");
		im.setComment("Schnurz piep egal. Na sowas da wird ja garnichts gemacht."
				+ "Und noch mehr Kommentare und scheiss um den Puffer aufzufuellen.");
		im.addArg("String", "name");
		im.addArg("int", "number", "eine Zahl");

		Java.createSource(itf);

	}
}
