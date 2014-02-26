package com.uwemeding.fuzzer.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * Helper to create a Java source class.
 *
 * @author uwe
 */
public class Java {

	private final static String OP = "{";
	private final static String CP = "}";

	public final static int STANDARD = 1;
	public final static int SEPARATE = 2;

	private static int mode;

	private static File baseDir;

	private final static String DATE;
	private final static String YEAR;
	private static String COPYRIGHT;

	private final static Object lock = new Object();

	static {
		SimpleDateFormat DAY_FMT = new SimpleDateFormat("MM/dd/yyyy");
		DATE = DAY_FMT.format(new Date());

		SimpleDateFormat YEAR_FMT = new SimpleDateFormat("yyyy");
		YEAR = DAY_FMT.format(new Date());

		COPYRIGHT = "Copyright (c) " + YEAR + " Meding Software Technik -- All Rights Reserved.";

		mode = STANDARD;
	}

	public static void setIndentMode(int indentMode) {
		mode = indentMode;
	}

	public static void setBaseDirectory(File path) {
		baseDir = path;
	}

	public static Java.CLASS createClass(String type, String name) {
		Java.CLASS c = new Java.CLASS(type, name);
		return (c);
	}

	public static Java.INTERFACE createInterface(String type, String name) {
		Java.INTERFACE c = new Java.INTERFACE(type, name);
		return (c);
	}

	/**
	 * Create the source file for a given class
	 *
	 * @param clazz is the class description
	 * @throws java.io.IOException
	 */
	public static synchronized void createSource(Java._ClassBody clazz)
			throws IOException {
		if (baseDir == null) {
			throw new IOException("Base directory for output not set, use 'setBaseDirectory'");
		}
		String pkg = clazz.getPackage();
		if (pkg == null) {
			throw new IOException("Class package cannot be null");
		}
		pkg = pkg.replace('.', '/');

		File path = new File(baseDir, pkg);
		path.mkdirs();
		try (PrintWriter fp = new PrintWriter(new FileWriter(new File(path, clazz.getName() + ".java")))) {
			clazz.emit(0, fp);
		}
	}

	/**
	 * Description of the Method
	 *
	 * @param fp Description of the Parameter
	 * @param text Description of the Parameter
	 * @param indent Description of the Parameter
	 * @param type Description of the Parameter
	 */
	private static void emitCommentIndentN(PrintWriter fp, String text, int indent, boolean type) {
		synchronized (lock) {
			String cc = type ? "/**" : "/*";
			fp.println(cc);

			String comment = emitCommentIndentNOnly(fp, text, indent);

			fp.println(comment + "/");
		}
	}

	/**
	 * Description of the Method
	 *
	 * @param fp Description of the Parameter
	 * @param indent Description of the Parameter
	 */
	private static void emitFinishCommentIndentN(PrintWriter fp, int indent) {
		final String spaces = "                                                                   ";
		synchronized (lock) {
			String comment = spaces.substring(0, indent) + " */";
			fp.println(comment);
		}
	}

	/**
	 * Description of the Method
	 *
	 * @param fp Description of the Parameter
	 * @param text Description of the Parameter
	 * @param indent Description of the Parameter
	 * @return Description of the Return Value
	 */
	private static String emitCommentIndentNOnly(PrintWriter fp, String text, int indent) {
		synchronized (lock) {
			return (emitCommentIndentNOnly(fp, text, indent, true));
		}
	}

	private static String emitCommentIndentNOnly(PrintWriter fp, String text, int indent, boolean cooked) {
		final String spaces = "                                                                   ";
		synchronized (lock) {
			String is = spaces.substring(0, indent);
			String comment = is + " *";

			// remove repeating character
			boolean loop = text.length() > 1;
			while (loop) {
				char start = text.charAt(0);
				int spos = 0;
				int count = 0;
				for (int i = 1; i < text.length(); i++) {
					if (start == text.charAt(i)) {
						count++;
					} else {
						if (count > 5) {
							break;
						}

						start = text.charAt(i);
						spos = i;

						count = 0;
					}
				}

				if (count > 5) {
					String ntext = text.substring(0, spos);
					ntext = ntext + text.substring(spos + count + 1);
					text = ntext;
				} else {
					loop = false;
				}
			}

			// map word breaks
			int len = 2;
			fp.print(comment);

			StringTokenizer st = new StringTokenizer(text, " \r\n");
			String word = ".";
			while (st.hasMoreTokens()) {
				word = st.nextToken();
				if (len + word.length() > 60) {
					fp.println();
					fp.print(comment);
					len = 2;
				}
				fp.print(" ");
				fp.print(word);
				len += word.length();
			}
			if (cooked && word.charAt(word.length() - 1) != '.') {
				fp.println(".");
			} else {
				fp.println();
			}

			return (comment);
		}
	}

	/**
	 * A Statement indicator.
	 */
	interface _Statement {

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		void emit(int indent, PrintWriter fp);
	}

	/**
	 * A no-block indicator.
	 */
	interface _NoBlock {
	}

	interface _NeedBlock {
	}

	interface _NeedSomething {
	}

	interface _NoIndent {
	}

	interface _IsInterface {
	}

	public static class _Block extends ArrayList<_Statement> {

		/**
		 * Adds a feature to the C attribute of the _StatementBlock object
		 *
		 * @param s The feature to be added to the C attribute
		 */
		public void addC(String s) {
			Java.C stmt = new Java.C(s);
			add(stmt);
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		public void emitIndent(int indent, PrintWriter fp) {
			final String spaces = "                                                               ";
			fp.print(spaces.substring(0, indent));
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		@SuppressWarnings("empty-statement")
		public void emit(int indent, PrintWriter fp) {
			int incr;
			if (this instanceof _NoBlock) {
				;
			} else {
				if (size() > 0 || this instanceof _NeedBlock) {
					if (mode == SEPARATE) {
						fp.println();
						if (size() > 1 || this instanceof _NeedBlock) {
							emitIndent(indent, fp);
						}
					} else {
						if (size() > 1 || this instanceof _NeedBlock) {
							fp.print(" ");
						} else {
							fp.println();
						}
					}
					if (size() > 1 || this instanceof _NeedBlock) {
						fp.println(OP);
					}
				}
			}

			if (size() == 0 && this instanceof _NeedSomething) {
				_Statement s = (_Statement) new Java.S("");
				fp.println();
				emitIndent(indent + 4, fp);
				s.emit(indent + 4, fp);
			} else {

				for (_Statement s : this) {

					// no indent means we are doing a litte indent
					if (s instanceof _NoIndent) {
						emitIndent(indent, fp);
						s.emit(indent, fp);
					} else {
						emitIndent(indent + 4, fp);
						s.emit(indent + 4, fp);
					}
				}
			}

			if (this instanceof _NoBlock) {
				;
			} else {
				if (size() > 1 || this instanceof _NeedBlock) {
					emitIndent(indent, fp);
					fp.println(CP);
				}
			}
		}
	}

	public static class _ClassBlock extends _Block {

		public CLASS addCLASS(String type, String name) {
			Java.CLASS c = new Java.CLASS(type, name);
			c.setTopLevel(false);
			add(c);
			return (c);
		}

		public INTERFACE addINTERFACE(String type, String name) {
			Java.INTERFACE c = new Java.INTERFACE(type, name);
			c.setTopLevel(false);
			add(c);
			return (c);
		}

		public Java.VAR addVAR(String modifier, String type, String name) {
			VAR v = new VAR(true, modifier, type, name);
			add(v);
			return (v);
		}

		public Java.VAR addVAR(String modifier, String type, String name, String init) {
			VAR v = new VAR(true, modifier, type, name, init);
			add(v);
			return (v);
		}
	}

	/**
	 * A statement block.
	 */
	public static class _StatementBlock extends _Block {

		public Java.VAR addVAR(String modifier, String type, String name) {
			VAR v = new VAR(false, modifier, type, name);
			add(v);
			return (v);
		}

		public Java.VAR addVAR(String modifier, String type, String name, String init) {
			VAR v = new VAR(false, modifier, type, name, init);
			add(v);
			return (v);
		}

		/**
		 * Adds a feature to the S attribute of the _StatementBlock object
		 *
		 * @param s The feature to be added to the S attribute
		 */
		public void addS(String s) {
			Java.S stmt = new Java.S(s);
			add(stmt);
		}

		public void addLine(String s) {
			Java.Line stmt = new Java.Line(s);
			add(stmt);
		}

		public void addRETURN(String expr) {
			Java.RETURN r = new Java.RETURN(expr);
			add(r);
		}

		public void addLABEL(String s) {
			Java.LABEL l = new Java.LABEL(s);
			add(l);
		}

		/**
		 * Adds a feature to the FOR attribute of the _StatementBlock object
		 *
		 * @param start The feature to be added to the FOR attribute
		 * @param condition The feature to be added to the FOR attribute
		 * @param iter The feature to be added to the FOR attribute
		 * @return Description of the Return Value
		 */
		public Java.FOR addFOR(String start, String condition, String iter) {
			Java.FOR f = new Java.FOR(start, condition, iter);
			add(f);
			return (f);
		}

		/**
		 * Adds a feature to the IF attribute of the _StatementBlock object
		 *
		 * @param cond The feature to be added to the IF attribute
		 * @return Description of the Return Value
		 */
		public Java.IF addIF(String cond) {
			Java.IF i = new Java.IF(this, cond);
			add(i);
			return (i);
		}

		/**
		 * Adds a feature to the WHILE attribute of the _StatementBlock object
		 *
		 * @param cond The feature to be added to the WHILE attribute
		 * @return Description of the Return Value
		 */
		public Java.WHILE addWHILE(String cond) {
			Java.WHILE w = new Java.WHILE(cond);
			add(w);
			return (w);
		}

		/**
		 * Adds a feature to the SWITCH attribute of the _StatementBlock object
		 *
		 * @param cond The feature to be added to the SWITCH attribute
		 * @return Description of the Return Value
		 */
		public Java.SWITCH addSWITCH(String cond) {
			Java.SWITCH w = new Java.SWITCH(cond);
			add(w);
			return (w);
		}

		/**
		 * Adds a feature to the DOWHILE attribute of the _StatementBlock object
		 *
		 * @param cond The feature to be added to the DOWHILE attribute
		 * @return Description of the Return Value
		 */
		public Java.DOWHILE addDOWHILE(String cond) {
			Java.DOWHILE w = new Java.DOWHILE(cond);
			add(w);
			return (w);
		}

		public Java.TRY addTRY() {
			Java.TRY t = new Java.TRY(this);
			add(t);
			return (t);
		}

	}

	/**
	 * A statement.
	 */
	public static class S implements _Statement {

		private String s;

		/**
		 * Constructor for the S object
		 *
		 * @param s Description of the Parameter
		 */
		public S(String s) {
			if (s != null) {
				this.s = s.trim();
			}
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		@Override
		public void emit(int indent, PrintWriter fp) {
			if (s == null) {
				fp.println();
			} else {
				fp.println(s + ";");
			}
		}
	}

	/**
	 * A line of code.
	 */
	public static class Line implements _Statement {

		private String s;

		/**
		 * Constructor for the S object
		 *
		 * @param s Description of the Parameter
		 */
		public Line(String s) {
			this.s = s;
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		@Override
		public void emit(int indent, PrintWriter fp) {
			if (s == null) {
				fp.println();
			} else {
				fp.println(s);
			}
		}
	}

	public static class LABEL implements _Statement, _NoIndent, _NoBlock {

		private String s;

		/**
		 * Constructor for the S object
		 *
		 * @param s Description of the Parameter
		 */
		public LABEL(String s) {
			this.s = s.trim();
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		@Override
		public void emit(int indent, PrintWriter fp) {
			fp.println(s + ":");
		}
	}

	public static class RETURN implements _Statement, _NoBlock {

		private String expr;

		public RETURN(String expr) {
			this.expr = expr.trim();
		}

		@Override
		public void emit(int indent, PrintWriter fp) {
			if (mode == SEPARATE) {
				fp.println("return " + expr + ";");
			} else {
				fp.println("return " + expr + ";");
			}
		}
	}

	/**
	 * A comment
	 */
	public static class C implements _Statement {

		private String s;

		/**
		 * Constructor for the C object
		 *
		 * @param s Description of the Parameter
		 */
		public C(String s) {
			this.s = s.trim();
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		@Override
		public void emit(int indent, PrintWriter fp) {
			Java.emitCommentIndentN(fp, s, indent, false);
		}
	}

	/**
	 * FOR statement
	 */
	public static class FOR extends _StatementBlock implements _Statement, _NeedSomething {

		private String start;
		private String condition;
		private String iter;

		/**
		 * Constructor for the FOR object
		 *
		 * @param start Description of the Parameter
		 * @param condition Description of the Parameter
		 * @param iter Description of the Parameter
		 */
		public FOR(String start, String condition, String iter) {
			this.start = start == null ? "" : start.trim();
			this.condition = condition == null ? "" : condition.trim();
			this.iter = iter == null ? "" : iter.trim();
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		@Override
		public void emit(int indent, PrintWriter fp) {
			fp.print("for(" + start + "; " + condition + "; " + iter + ")");
			super.emit(indent, fp);
		}
	}

	/**
	 * IF statement
	 */
	public static class IF extends _StatementBlock implements _Statement, _NeedSomething {

		private _StatementBlock parent;
		private String condition;

		/**
		 * Constructor for the IF object
		 *
		 * @param parent
		 * @param condition Description of the Parameter
		 */
		public IF(_StatementBlock parent, String condition) {
			this.parent = parent;
			if (condition == null) {
				throw new NullPointerException("condition cannot be null");
			}
			this.condition = condition.trim();
		}

		public ELSE addELSE() {
			Java.ELSE e = new Java.ELSE();
			parent.add(e);
			return (e);
		}

		public ELSE addELSEIF(String cond) {
			Java.ELSE e = new Java.ELSE(cond);
			parent.add(e);
			return (e);
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		@Override
		public void emit(int indent, PrintWriter fp) {
			fp.print("if(" + condition + ")");
			super.emit(indent, fp);
		}
	}

	public static class ELSE extends _StatementBlock implements _Statement, _NeedSomething {

		private String cond;
		private boolean just_else;

		public ELSE() {
			just_else = true;
		}

		public ELSE(String cond) {
			this.cond = cond.trim();
		}

		@Override
		public void emit(int indent, PrintWriter fp) {
			if (mode == SEPARATE) {
				if (just_else) {
					fp.print("else");
				} else {
					fp.println("else");
					emitIndent(indent, fp);
					fp.print("if(" + cond + ")");
				}
				super.emit(indent, fp);
			} else {
				if (just_else) {
					fp.print("else");
				} else {
					fp.print("else if(" + cond + ")");
				}
				super.emit(indent, fp);
			}

		}
	}

	/**
	 * WHILE statement
	 */
	public static class WHILE extends _StatementBlock implements _Statement, _NeedSomething {

		private String condition;

		/**
		 * Constructor for the WHILE object
		 *
		 * @param condition Description of the Parameter
		 */
		public WHILE(String condition) {
			if (condition == null) {
				throw new NullPointerException("condition cannot be null");
			}
			this.condition = condition.trim();
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		@Override
		public void emit(int indent, PrintWriter fp) {
			fp.print("while(" + condition + ")");
			super.emit(indent, fp);
		}
	}

	/**
	 * SWITCH statement
	 */
	public static class SWITCH extends _StatementBlock implements _Statement, _NeedBlock {

		private String condition;

		/**
		 * Constructor for the SWITCH object
		 *
		 * @param condition Description of the Parameter
		 */
		public SWITCH(String condition) {
			if (condition == null) {
				throw new NullPointerException("condition cannot be null");
			}
			this.condition = condition.trim();
		}

		/**
		 * Adds a feature to the CASE attribute of the SWITCH object
		 *
		 * @param cond The feature to be added to the CASE attribute
		 * @return Description of the Return Value
		 */
		public Java.CASE addCASE(String cond) {
			return (addCASE(false, cond));
		}

		public Java.CASE addCASE(boolean block, String cond) {
			Java.CASE w;
			if (block) {
				w = new Java.CaseBlock(cond);
			} else {
				w = new Java.CaseNoBlock(cond);
			}
			add(w);
			return (w);
		}

		/**
		 * Adds a feature to the DEFAULT attribute of the SWITCH object
		 *
		 * @return Description of the Return Value
		 */
		public Java.CASE addDEFAULT() {
			return (addDEFAULT(false));
		}

		public Java.CASE addDEFAULT(boolean block) {
			Java.CASE c = addCASE(block, "");
			c.setDefault(true);
			return (c);
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		@Override
		public void emit(int indent, PrintWriter fp) {
			fp.print("switch(" + condition + ")");
			super.emit(indent, fp);
		}
	}

	/**
	 * CASE statement
	 */
	public static class CaseBlock extends CASE implements _NeedBlock {

		public CaseBlock(String cond) {
			super(cond);
		}
	}

	public static class CaseNoBlock extends CASE implements _NoBlock, _NoIndent {

		public CaseNoBlock(String cond) {
			super(cond);
		}
	}

	public static class CASE extends _StatementBlock implements _Statement {

		private String condition;
		private boolean defCase;
		private boolean fallThru;

		/**
		 * Constructor for the CASE object
		 *
		 * @param condition Description of the Parameter
		 */
		public CASE(String condition) {
			if (condition == null) {
				throw new NullPointerException("condition cannot be null");
			}
			this.condition = condition.trim();
		}

		/**
		 * Sets the fallThru attribute of the CASE object
		 *
		 * @param fallThru The new fallThru value
		 */
		public void setFallThru(boolean fallThru) {
			this.fallThru = fallThru;
		}

		/**
		 * Sets the default attribute of the CASE object
		 *
		 * @param defCase The new default value
		 */
		public void setDefault(boolean defCase) {
			this.defCase = defCase;
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		@Override
		public void emit(int indent, PrintWriter fp) {
			if (defCase) {
				fp.print("default:");
			} else {
				fp.print("case " + condition + ":");
			}
			if (this instanceof _NeedBlock && mode == SEPARATE) {
				super.emitIndent(indent + 4, fp);
			}
			super.emit(indent, fp);
			super.emitIndent(indent + 4, fp);
			if (fallThru) {
				fp.println("// fall through ");
			} else {
				fp.println("break;");
			}
		}
	}

	/**
	 * DO WHILE statement
	 */
	public static class DOWHILE extends _StatementBlock implements _Statement, _NeedSomething {

		private String condition;

		/**
		 * Constructor for the DOWHILE object
		 *
		 * @param condition Description of the Parameter
		 */
		public DOWHILE(String condition) {
			if (condition == null) {
				throw new NullPointerException("condition cannot be null");
			}
			this.condition = condition.trim();
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		@Override
		public void emit(int indent, PrintWriter fp) {
			fp.print("do");
			super.emit(indent, fp);
			super.emitIndent(indent, fp);
			fp.println("while(" + condition + ");");
		}
	}

	public static class TRY extends _StatementBlock implements _Statement, _NeedBlock {

		_StatementBlock parent;

		public TRY(_StatementBlock sb) {
			parent = sb;
		}

		public Java.CATCH addCATCH(String ex, String name) {
			Java.CATCH c = new Java.CATCH(ex, name);
			parent.add(c);
			return (c);
		}

		public Java.CATCH addFINALLY() {
			Java.CATCH c = new Java.CATCH();
			parent.add(c);
			return (c);
		}

		@Override
		public void emit(int indent, PrintWriter fp) {
			fp.print("try");
			super.emit(indent, fp);
		}
	}

	public static class CATCH extends _StatementBlock implements _Statement, _NeedBlock {

		private String ex;
		private String name;
		private boolean fin;

		public CATCH() {
			fin = true;  // this is the finally clue
		}

		public CATCH(String ex, String name) {
			this.ex = ex.trim();
			this.name = name.trim();
		}

		@Override
		public void emit(int indent, PrintWriter fp) {
			if (fin) {
				fp.print("finally");
			} else {
				fp.print("catch(" + ex + " " + name + ")");
			}
			super.emit(indent, fp);
		}
	}

	public static class VAR extends _ClassBlock implements _Statement, _NoBlock {

		private String modifier;
		private String type;
		private String name;
		private String init;
		private String comment;
		private boolean isClassVar;

		public VAR(boolean isClassVar, String modifier, String type, String name) {
			this.modifier = modifier == null ? null : modifier.trim();
			this.type = type.trim();
			this.name = name.trim();
			this.isClassVar = isClassVar;
		}

		public VAR(boolean isClassVar, String modifier, String type, String name, String init) {
			this(isClassVar, modifier, type, name);
			this.init = init.trim();
		}

		public void setComment(String comment) {
			if (comment != null) {
				this.comment = comment.trim();
			}
		}

		@Override
		public void emit(int indent, PrintWriter fp) {
			boolean jdoc = type.indexOf("private") < 0;
			if (comment != null) {
				emitCommentIndentN(fp, comment, indent, jdoc);
				if (isClassVar) {
					super.emitIndent(indent, fp);
				}
			}
			if (modifier == null) {
				fp.print(type + " " + name);
			} else {
				fp.print(modifier + " " + type + " " + name);
			}

			/*
			 // if we have a String we'll need to cook the initializer a little
			 if((type.indexOf("String") >= 0) && (init != null))
			 {
			 if(init.startsWith("\"") || init.equals("null"))
			 fp.print(" = "+init);
			 else
			 fp.print(" = \""+init+"\"");
			 }
			 else
			 */
			if (init != null) {
				fp.print(" = " + init);
			}
			fp.println(";");
		}
	}

	/**
	 * a method
	 */
	public static class METHOD extends _StatementBlock implements _Statement, _NeedBlock {

		private List<Arg> args;
		private List<Throw> thrws;
		private String modifier;
		private String retType;
		private String methodName;
		private String comment;
		private boolean isInterface;
		private String returns;
		private boolean isCLinit;

		/**
		 * Constructor for the Method object
		 *
		 * @param modifier
		 * @param retType Description of the Parameter
		 * @param methodName Description of the Parameter
		 */
		public METHOD(String modifier, String retType, String methodName) {
			if (modifier == null) {
				this.modifier = "";
			} else {
				this.modifier = modifier.trim() + " ";
			}
			if (retType == null) {
				this.retType = "";
			} else {
				this.retType = retType.trim();
			}
			if (methodName == null) {
				methodName = "";
			}
			this.methodName = methodName.trim();
			args = new ArrayList<>();
			thrws = new ArrayList<>();
		}

		public METHOD(String modifier, String retType, String methodName, boolean isInterface) {
			this(modifier, retType, methodName);
			this.isInterface = isInterface;
		}

		public void setIsCLinit(boolean mode) {
			isCLinit = mode;
		}

		public boolean isCLinit() {
			return (isCLinit);
		}

		/**
		 * Sets the comment attribute of the Method object
		 *
		 * @param comment The new comment value
		 */
		public void setComment(String comment) {
			if (comment != null) {
				this.comment = comment.trim();
			}
		}

		public void setReturnComment(String comment) {
			if (comment != null) {
				this.returns = comment.trim();
			}
		}

		/**
		 * Adds a feature to the Arg attribute of the Method object
		 *
		 * @param type The feature to be added to the Arg attribute
		 * @param name The feature to be added to the Arg attribute
		 */
		public void addArg(String type, String name) {
			args.add(new Arg(type, name));
		}

		/**
		 * Adds a feature to the Arg attribute of the Method object
		 *
		 * @param type The feature to be added to the Arg attribute
		 * @param name The feature to be added to the Arg attribute
		 * @param comment The feature to be added to the Arg attribute
		 */
		public void addArg(String type, String name, String comment) {
			args.add(new Arg(type, name, comment));
		}

		/**
		 * Adds a feature to the THROWS attribute of the Method object
		 *
		 * @param name The feature to be added to the THROWS attribute
		 */
		public void addTHROWS(String name) {
			thrws.add(new Throw(name));
		}

		public void addTHROWS(String name, String comment) {
			thrws.add(new Throw(name, comment));
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		@Override
		@SuppressWarnings("empty-statement")
		public void emit(int indent, PrintWriter fp) {
			String delim;
			fp.println();
			emitIndent(indent, fp);
			fp.println("/**");
			if (isCLinit()) {
				if (comment == null) {
					comment = "Static initializer";
				}
				Java.emitCommentIndentNOnly(fp, comment, indent);
			} else {
				if (comment == null) {
					comment = "Description of the method.";
				}
				Java.emitCommentIndentNOnly(fp, comment, indent);
				for (Arg arg : args) {
					arg.emitComment(indent, fp);
				}
				if (retType.length() > 0 && retType.indexOf("void") < 0) {
					String c;
					if (returns == null) {
						c = "a " + retType;
					} else {
						c = returns;
					}
					Java.emitCommentIndentNOnly(fp, "@return " + c, indent, false);
				}

				if (thrws.size() > 0) {
					for (Throw thrw : thrws) {
						thrw.emitComment(indent, fp);
					}
				}
			}

			Java.emitFinishCommentIndentN(fp, indent);
			emitIndent(indent, fp);

			if (isCLinit()) {
				fp.print("static ");
			} else {
				fp.print(modifier + retType + " " + methodName + "(");
				delim = "";
				for (Arg arg : args) {
					fp.print(delim);
					arg.emit(fp);
					delim = ", ";
				}
				fp.print(")");
				if (thrws.size() > 0) {
					fp.println();
					emitIndent(indent + 4, fp);
					fp.print("throws ");
					delim = "";
					for (Throw thrw : thrws) {
						fp.print(delim);
						delim = ", ";
						thrw.emit(fp);
					}
				}
			}

			if (isInterface) {
				fp.println(";");
			} else {
				if (mode == SEPARATE)
                    ; //fp.println();
				else {
					fp.print(" ");
				}
				super.emit(indent, fp);
			}
		}
	}

	/**
	 * A throw
	 */
	public static class Throw {

		private String name;
		private String comment;

		/**
		 * Constructor for the Arg object
		 *
		 * @param name Description of the Parameter
		 */
		public Throw(String name) {
			if (name == null) {
				throw new NullPointerException("Throw name cannot be null");
			}
			this.name = name.trim();
		}

		/**
		 * Constructor for the Arg object
		 *
		 * @param name Description of the Parameter
		 * @param comment Description of the Parameter
		 */
		public Throw(String name, String comment) {
			this(name);
			this.comment = comment.trim();
		}

		/**
		 * Description of the Method
		 *
		 * @param fp Description of the Parameter
		 */
		public void emit(PrintWriter fp) {
			fp.print(name);
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		public void emitComment(int indent, PrintWriter fp) {
			if (comment == null) {
				comment = "";
			}
			Java.emitCommentIndentNOnly(fp, "@throws " + name + " " + comment, indent, false);
		}
	}

	public static class Arg {

		private String type;
		private String name;
		private String comment;

		/**
		 * Constructor for the Arg object
		 *
		 * @param type Description of the Parameter
		 * @param name Description of the Parameter
		 */
		public Arg(String type, String name) {
			if (type == null) {
				throw new NullPointerException("Argument type cannot be null");
			}
			this.type = type;
			if (name == null) {
				throw new NullPointerException("Argument name cannot be null");
			}
			this.name = name.trim();
		}

		/**
		 * Constructor for the Arg object
		 *
		 * @param type Description of the Parameter
		 * @param name Description of the Parameter
		 * @param comment Description of the Parameter
		 */
		public Arg(String type, String name, String comment) {
			this(type, name);
			this.comment = comment.trim();
		}

		/**
		 * Description of the Method
		 *
		 * @param fp Description of the Parameter
		 */
		public void emit(PrintWriter fp) {
			fp.print(type + " " + name);
		}

		/**
		 * Description of the Method
		 *
		 * @param indent Description of the Parameter
		 * @param fp Description of the Parameter
		 */
		public void emitComment(int indent, PrintWriter fp) {
			if (comment == null) {
				comment = "is " + type;
			}
			Java.emitCommentIndentNOnly(fp, "@param " + name + " " + comment, indent, false);
		}
	}

	public static class INTERFACE extends _ClassBody
			implements _Statement, _NeedBlock, _IsInterface {

		public INTERFACE(String type, String name) {
			super(type, name);
		}

		public void addEXTENDS(String name) {
			impls.add(name);
		}

		public void addEXTENDS(String[] names) {
			if (names == null) {
				return;
			}
			for (String name : names) {
				addEXTENDS(name);
			}
		}

		public Java.METHOD addMETHOD(String retType, String name) {
			Java.METHOD f = new Java.METHOD(null, retType, name, true);
			add(f);
			return (f);
		}
	}

	public static class CLASS extends _ClassBody implements _Statement, _NeedBlock {

		String name;

		public CLASS(String type, String name) {
			super(type, name);
			this.name = name;
		}

		public void addIMPL(String name) {
			impls.add(name);
		}

		public void addIMPL(String[] names) {
			if (names == null) {
				return;
			}
			for (String name1 : names) {
				addIMPL(name1);
			}
		}

		public void setExtends(String name) {
			super.extendName = name;
		}

		public Java.METHOD addCLINIT() {
			Java.METHOD f = new Java.METHOD(null, null, null);
			f.setIsCLinit(true);
			add(f);
			return (f);
		}

		public Java.METHOD addCTOR(String modifier) {
			Java.METHOD f = new Java.METHOD(modifier, null, name);
			add(f);
			return (f);
		}

		public Java.METHOD addMETHOD(String retType, String name) {
			Java.METHOD f = new Java.METHOD(null, retType, name);
			add(f);
			return (f);
		}

		/**
		 * Adds getters and setters for a given property with the given data
		 * type and default value;
		 *
		 * @param	name	attribute name
		 * @param	type	attribute data type
		 * @param	def	attribute default value
		 *
		 */
		public void addProperty(String name, String type, String def) {
			VAR var = addVAR("private", type, "_" + name, def);
			String getMethod = makeGetter(name);
			String setMethod = makeSetter(name);
			METHOD getter = addMETHOD("public", type, getMethod);
			getter.addS("return _" + name);
			METHOD setter = addMETHOD("public", "void", setMethod);
			setter.addArg(type, name);
			setter.addS("_" + name + "=" + name);
		}

		public Java.METHOD addMETHOD(String modifier, String retType, String name) {
			Java.METHOD f;
			if (modifier.indexOf("abstract") < 0) {
				f = new Java.METHOD(modifier, retType, name, false);
			} else {
				f = new Java.METHOD(modifier, retType, name, true);
			}
			add(f);
			return (f);
		}

		/**
		 * Create a getter function for an attribute.
		 *
		 * @param attr the attribute
		 * @return the getter
		 */
		private String makeGetter(String attr) {
			StringBuilder sb = new StringBuilder((attr.length() + 3));
			sb.append("get");
			sb.append(attr.substring(0, 1).toUpperCase());
			sb.append(attr.substring(1));
			return sb.toString();
		}

		/**
		 * Create a setter function for an attribute.
		 *
		 * @param attr the attribute
		 * @return the setter
		 */
		private String makeSetter(String attr) {
			StringBuilder sb = new StringBuilder((attr.length() + 3));
			sb.append("set");
			sb.append(attr.substring(0, 1).toUpperCase());
			sb.append(attr.substring(1));
			return sb.toString();
		}

	}

	public static class _ClassBody extends _ClassBlock implements _Statement, _NeedBlock {

		protected List<String> impls;
		private final String type;
		private final String name;
		private String comment;
		private String packageName;
		protected String extendName;
		private final Set<String> imports;
		private boolean topLevel;
		private String author;
		private String version;
		private String since;

		public _ClassBody(String type, String name) {
			this.type = type.trim();
			this.name = name.trim();
			this.impls = new ArrayList<>();
			this.imports = new TreeSet<>();
			this.topLevel = true;
		}

		public String getName() {
			return (name);
		}

		public void setComment(String comment) {
			if (comment != null) {
				this.comment = comment.trim();
			}
		}

		public void setTopLevel(boolean topLevel) {
			this.topLevel = topLevel;
		}

		public void addIMPORT(String name) {
			if (name != null) {
				imports.add(name);
			}
		}

		public void addIMPORT(String[] names) {
			if (names == null) {
				return;
			}
			for (String name1 : names) {
				addIMPORT(name1);
			}
		}

		public void setPackage(String packageName) {
			this.packageName = packageName.trim();
		}

		public String getPackage() {
			return (packageName);
		}

		public void setAuthor(String author) {
			this.author = author.trim();
		}

		public void setVersion(String version) {
			this.version = version.trim();
		}

		public void setSince(String since) {
			this.since = since.trim();
		}

		@Override
		public void emit(int indent, PrintWriter fp) {
			String delim;
			if (topLevel) {
				Java.emitCommentIndentN(fp, COPYRIGHT, 0, false);
				fp.println();
				if (packageName != null) {
					fp.println("package " + packageName + ";");
				}
				fp.println();
				for (String i : imports) {
					fp.println("import " + i + ";");
				}
				fp.println();
			}
			fp.println("/**");
			if (comment == null) {
				comment = "Description of the class.";
			}
			Java.emitCommentIndentNOnly(fp, comment, indent);

			if (version != null) {
				Java.emitCommentIndentNOnly(fp, "@version " + version, indent, false);
			}
			if (author != null) {
				Java.emitCommentIndentNOnly(fp, "@author " + author, indent, false);
			}
			if (since != null) {
				Java.emitCommentIndentNOnly(fp, "@since " + since, indent, false);
			}

			Java.emitFinishCommentIndentN(fp, indent);
			emitIndent(indent, fp);
			if (this instanceof _IsInterface) {
				fp.print(type + " interface " + name);
			} else {
				fp.print(type + " class " + name);
			}
			if (extendName != null) {
				fp.println();
				emitIndent(indent + 4, fp);
				fp.print("extends " + extendName);
			}
			if (impls.size() > 0) {
				fp.println();
				emitIndent(indent + 4, fp);

				if (this instanceof _IsInterface) {
					fp.print("extends ");
				} else {
					fp.print("implements ");
				}
				delim = "";
				for (String name : impls) {
					fp.print(delim + name);
					delim = ", ";
				}
			}

			super.emit(indent, fp);
		}
	}

}
