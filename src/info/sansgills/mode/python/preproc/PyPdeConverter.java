package info.sansgills.mode.python.preproc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

/**
 * 
 * So, this class extends antlr's listener class in order to create the
 * functionality we need. We're being fed parsed python code with lexical
 * & semantic info attached, and we need to go through it and futz with 
 * some things to make Processing code behave as expected.
 * 
 * Rather than reformatting the whole script as a class definition and 
 * instantiating one to use as a PApplet, we inject a bunch of functions
 * that reference a special PApplet instance, tell it to use the user-defined 
 * PApplet functions, and then run it normally. However, there are a few things
 * that this doesn't work for: instance variables and a few things that are
 * polymorphic (I think?) in java- frameRate is a variable and a function, for
 * instance.
 * 
 * So, this class fixes those.
 * 
 */

public class PyPdeConverter extends PyPdeBaseListener {
	TokenStreamRewriter rewriter;

	//ugly, but whatever
	static final Set<String> instanceVars = new HashSet<String>(Arrays.asList(new String[] { "mouseX", "mouseY", "pmouseX", "pmouseY", "mouseButton", "keyCode", "key", "pixels", "width", "height", "displayWidth", "displayHeight", "focused", "frameCount" }));
	static final Set<String> wonkyVars = new HashSet<String>(Arrays.asList(new String[] { "mousePressed", "keyPressed" }));

	TokenStream tokens;

	Set<String> globals = new HashSet<String>();
	ArrayList<String> imports = new ArrayList<String>();

	int indentLevel = 0;
	int attrRefLevel = 0;

	boolean hasIssues = false;
	boolean staticMode = true;
	public boolean usesOpenGL = false;

	String result;

	public PyPdeConverter(TokenStream tokens) {
		this.rewriter = new TokenStreamRewriter(tokens);
		this.tokens = tokens;
	}

	//you can never escape the regex! mwahaha
	public static final Pattern indent = Pattern.compile("\r?\n");

	public String getText() {
		if (result == null) {
			String temp = rewriter.getText();
			if (staticMode) {
				//no setup function found, so we'll turn the whole sketch into one
				result = "def setup():\n\t" + indent.matcher(temp).replaceAll("\n\t") + "\n";
			} else {
				result = temp;
			}
		}
		return result;
	}

	public String[] getLibraries() {
		return imports.toArray(new String[0]);
	}

	//fiddle with certain PApplet variables
	//TODO yell at the user when they try to assign to things
	@Override
	public void enterAtom(PyPdeParser.AtomContext ctx) {
		PyPdeParser.IdentifierContext id = ctx.identifier();
		if (id != null) {
			String text = id.getText();
			//hopefully this will work without java string nonsense
			if (instanceVars.contains(text)) {
				//one of our instance variables;
				//replace mouseX with __applet__.mouseX, etc.
				rewriter.insertBefore(id.IDENTIFIER().getSymbol(), "__applet__."); //woo dirty hacks!
			} else if (wonkyVars.contains(text)) {
				//one of the obnoxious variables with name conflicts
				//replace mousePressed with getmousePressed()
				//can't do __applet__.mousePressed Jython thinks that refers to the function
				rewriter.insertBefore(id.IDENTIFIER().getSymbol(), "get");
				rewriter.insertAfter(id.IDENTIFIER().getSymbol(), "()");
			} else if (text.equals("frameRate")) {
				//special handling; if it's a call, use it as frameRate()
				//if it's not, use it as a call to getframeRate()
				boolean isCall;
				for (int i = id.start.getTokenIndex();; i++) {
					int type = tokens.get(i + 1).getType();
					if (type == PyPdeParser.LPAREN) {
						isCall = true;
						break;
					} else if (type == PyPdeParser.WS) {
						continue;
					} else {
						isCall = false;
						break;
					}
				}
				if (!isCall) {
					rewriter.insertBefore(id.IDENTIFIER().getSymbol(), "get");
					rewriter.insertAfter(id.IDENTIFIER().getSymbol(), "()");
				}
			}
		}
	}

	private static Pattern getArgs = Pattern.compile("OPENGL|P3D|P2D");

	@Override
	public void enterCall(PyPdeParser.CallContext ctx) {
		if (ctx.primary() instanceof PyPdeParser.PrimaryAtomContext) {
			if (ctx.primary().getText().equals("size")) {
				if (getArgs.matcher(ctx.getText()).find()) {
					usesOpenGL = true;
				}
			}
		}

	}

	@Override
	public void enterComplexSuite(PyPdeParser.ComplexSuiteContext ctx) {
		indentLevel++;
		rewriter.delete(ctx.INDENT().getSymbol());	//indent
		rewriter.delete(ctx.DEDENT().getSymbol());	//dedent
	}

	@Override
	public void exitComplexSuite(PyPdeParser.ComplexSuiteContext ctx) {
		indentLevel--;
	}

	@Override
	public void enterFuncdef(PyPdeParser.FuncdefContext ctx) {
		if (indentLevel == 0) {
			if (ctx.identifier().getText().equals("setup")) {
				staticMode = false;
				//setup is now defined, so we don't need to mess with things
			}
		}
	}

	@Override
	public void enterModule(PyPdeParser.ModuleContext ctx) {
		imports.add(ctx.getText());
	}
}
