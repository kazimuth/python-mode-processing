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
 * Class to handle the building of python files.
 * Given that python runs as an interpreter, all this does is handle preprocessing
 * and write to an output file.
 * 
 * See BUILD.md for notes.
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
	
	//build tracking
	private int buildnumber;
	private static int buildstotal = 0;
	
	public PythonBuild(Sketch sketch, PythonMode mode){
		this.sketch = sketch;
		this.mode = mode;
		
		buildnumber = buildstotal;
		buildstotal++;
	}
	
	/*
	 * Preprocess the sketch- turn the .pde files into valid python.
	 */
	public void build() throws Exception{
		
		StringBuilder program = new StringBuilder();
		
		SketchCode[] parts = sketch.getCode(); //fix'd
		
		//concatenate code strings
		//TODO do this in some sort of vaguely intelligent way
		for(int i = parts.length-1; i >= 0; i--){	//iterate backwards... it seemed like a good idea at the time
			program.append("\n");
			program.append(parts[i].getProgram());
		}
		
		resultProgram = preprocess(program);
		
		
		//write output file
		binFolder = sketch.makeTempFolder();
		
		outFile = new File(binFolder.getAbsolutePath()
				+ File.separator
				+ sketch.getName().toLowerCase()
				+ ".py");
		outFile.createNewFile();
		
		
		PrintWriter writer = new PrintWriter(outFile);
		writer.print(resultProgram);
		writer.close();
	}
	
	
	private static Pattern whitespace = Pattern.compile("\\s*");
	/*
	 * Turn .pde into valid python
	 * 
	 * Now with antlr!
	 */
	private String preprocess(StringBuilder program){
		if(whitespace.matcher(program).matches()){
			return "def setup():\n\tpass\n\n"; //empty sketch; TODO fix hack
		}
		try{
			program.append("\n"); //to be safe
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
			
			Set<String> knownPythonLibraries = PythonMode.getPythonLibraries();
			
			pythonLibs = "";
			javaLibs = "";
			
			for(String lib : libraries){
				if(knownPythonLibraries.contains(lib)){
					//it's a python library; for now, I'm only handling ones built into jython, so no need to do anything
				}else{
					//normal java library; we can use the normal library infrastructure
					int dot = lib.lastIndexOf('.');
				    String entry = (dot == -1) ? lib : lib.substring(0, dot);
				    Library library = mode.getLibrary(entry);
				    javaLibs += library.getJarPath() + System.getProperty("path.separator");
				}
			}
			
			if(pythonLibs.equals("")){
				pythonLibs = null;
			}
			if(javaLibs.equals("")){
				javaLibs = null;
			}
			return result;
			
		}catch(Exception e){
			System.err.println("Preprocessing failed.");
			e.printStackTrace();
			return null;
		}
	}
	
	public String getJavaLibraries(){
		return javaLibs;
	}
	public boolean hasJavaLibraries(){
		return javaLibs != null;
	}
	
	
	public String getPythonLibraries(){
		return pythonLibs;
	}
	public boolean hasPythonLibraries(){
		return pythonLibs != null;
	}
	
	/*
	 * The output code string
	 */
	public String getResultString(){
		return resultProgram;
	}
	
	/*
	 * The output file path
	 */
	public String getResultFile(){
		return outFile.getAbsolutePath();
	}
	
	/*
	 * 
	 */
	public int getBuildNumber(){
		return buildnumber;
	}
	
	/*
	 * Classes used to run the build.
	 */
	public String getClassPath() {
		return classPath; //computed during preprocessing
	}
	
	public String getName() {
		return sketch.getName();
	}
	
	/*
	 * TODO
	 */
	public String getJavaLibraryPath(){
		return "";
	}
}
