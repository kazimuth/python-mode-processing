package info.sansgills.mode.python;

import processing.app.Base;
import processing.core.PApplet;

/**
 * 
 * Class to handle the running of sketches. Starts a new ProcessingJythonWrapper
 * process and interfaces between it and the PDE.
 * 
 * TODO REPL?
 * 
 * 
 */

public class PythonRunner {
	PythonBuild build;				// the python source we're working with
	PythonEditor editor;
	
	Process sketchProcess;
	
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
