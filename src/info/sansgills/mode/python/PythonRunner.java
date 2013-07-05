package info.sansgills.mode.python;

import info.sansgills.mode.python.wrapper.PythonPApplet;

import org.python.core.*;
import org.python.util.InteractiveConsole;

import processing.app.Base;

/**
 * 
 * Class to handle the running of sketches. Starts a new ProcessingJythonWrapper
 * process and interfaces between it and the PDE.
 * 
 * TODO REPL?
 * 
 * Architecture note: Processing.py has a 'PApplet Jython Driver' class that
 * contains a private Python interpreter. It injects the PApplet variables and
 * methods into this interpreter, updating whenever it needs to. It runs the
 * input python directly, without modification.
 * 
 * I'm using a different approach, similar to vanilla Processing: preprocess the
 * .pde input into a valid python class inheriting from PApplet, run the
 * resulting code through an interpreter, and extract the class to create a
 * final Java object to run as a PApplet.
 * 
 */

public class PythonRunner {
	PythonBuild build;				// the python source we're working with
	PythonEditor editor;
	
	
	final String modeName = "PythonMode";	//for modifying python classpath
	final String sep = System.getProperty("file.separator");

	public PythonRunner(PythonBuild build, PythonEditor editor) {
		this.build = build;
		this.editor = editor;
	}

	/*
	 * Run the code.
	 */
	public void launch(boolean present) {		
	}

	/*
	 * Kill the code.
	 */
	public void close() {
		
	}
}
