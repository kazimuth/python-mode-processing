package info.sansgills.mode.python;

import java.io.File;
import java.io.PrintWriter;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import processing.app.Base;
import processing.app.Library;
import processing.app.Sketch;
import processing.app.SketchCode;

import info.sansgills.mode.python.preproc.*;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * 
 * Class to handle the building of python files. Given that python runs as an
 * interpreter, all this does is handle preprocessing and write to an output
 * file.
 * 
 */

public class PythonBuild {
	Sketch sketch;
	String resultProgram;

	PythonMode mode;

	File binFolder;
	File outFile;

	String classPath, pythonLibs, javaLibs;

	boolean usesOpenGL;

	//build tracking, not that this actually does anything
	private int buildnumber;
	private static int buildstotal = 0;

	public PythonBuild(Sketch sketch, PythonMode mode) {
		this.sketch = sketch;
		this.mode = mode;

		buildnumber = buildstotal;
		buildstotal++;
	}

	/*
	 * Preprocess the sketch- turn the .pde files into valid python.
	 */
	public void build() throws Exception {
		//to hold all the code
		StringBuilder program = new StringBuilder();
		SketchCode[] parts = sketch.getCode();

		//concatenate code strings
		//TODO do this in some sort of vaguely intelligent way
		for (int i = parts.length - 1; i >= 0; i--) {	//iterate backwards... it seemed like a good idea at the time
			program.append("\n");
			program.append(parts[i].getProgram());
		}

		//send to preprocessor
		resultProgram = preprocess(program);

		//create output folder
		binFolder = sketch.makeTempFolder();

		//create & write to output file
		outFile = new File(binFolder.getAbsolutePath() + File.separator + sketch.getName().toLowerCase() + ".py");
		outFile.createNewFile();

		PrintWriter writer = new PrintWriter(outFile);
		writer.print(resultProgram);
		writer.close();
	}

	/*
	 * Turn .pde files into valid python and extract used libraries See
	 * PyPdeConverter / generated antlr code for the actual preprocessing logic.
	 */
	private String preprocess(StringBuilder program) throws Exception {
		if (Pattern.matches("\\s*", program)) {
			return "def setup():\n\tpass\n\n";
		}
		try {
			program.append("\n"); //to be safe

			//now for the antlr
			PyPdeLexer lexer = new PyPdeLexer(new ANTLRInputStream(program.toString()));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PyPdeParser parser = new PyPdeParser(tokens);
			ParseTree tree = parser.script();
			PyPdeConverter converter = new PyPdeConverter(tokens);
			ParseTreeWalker walker = new ParseTreeWalker();
			walker.walk(converter, tree);

			String result = converter.getText();

			String[] libraries = converter.getLibraries();

			usesOpenGL = converter.usesOpenGL;

			pythonLibs = "";
			javaLibs = "";

			for (String lib : libraries) {
				if (mode.isPythonLibrary(lib)) {
					//it's a python library; for now, I'm only handling ones built into jython, so no need to do anything
				} else {
					//normal java library; we can use the normal library infrastructure
					int dot = lib.lastIndexOf('.');
					String entry = (dot == -1) ? lib : lib.substring(0, dot);
					Library library = mode.getLibrary(entry);
					if (library == null) {
						System.err.println("I don't know about the library "+lib+" so it might not work. Sorry.");
						continue;
					}
					javaLibs += library.getJarPath() + System.getProperty("path.separator");
				}
			}

			if (pythonLibs.equals("")) {
				pythonLibs = null;
			}
			if (javaLibs.equals("")) {
				javaLibs = null;
			}
			return result;

		} catch (Exception e) {
			System.err.println("Preprocessing failed.");
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * Access to extracted library information
	 */
	public String getJavaLibraries() {
		return javaLibs;
	}

	public boolean hasJavaLibraries() {
		return javaLibs != null;
	}

	public String getPythonLibraries() {
		return pythonLibs;
	}

	public boolean hasPythonLibraries() {
		return pythonLibs != null;
	}

	/*
	 * The output code string, properly formatted and whatnot.
	 */
	public String getResultString() {
		return resultProgram;
	}

	/*
	 * The output file path
	 */
	public String getResultFile() {
		return outFile.getAbsolutePath();
	}

	/*
	 * This may eventually tie in to some global thing... or not
	 */
	public int getBuildNumber() {
		return buildnumber;
	}

	/*
	 * Java classes used to run the build.
	 */
	public String getClassPath() {
		return classPath; //computed during preprocessing
	}

	/*
	 * ...
	 */
	public String getName() {
		return sketch.getName();
	}

	/*
	 * I'm not sure what the java library path actually is, so, leaving this
	 * blank.
	 */
	public String getJavaLibraryPath() {
		return "";
	}
}
