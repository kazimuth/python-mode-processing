package info.sansgills.mode.python;

import org.python.core.*;
import org.python.util.InteractiveConsole;

/**
 * 
 * Class to handle the running of sketches. Note that, for now, this does not
 * run the sketch in a new process like in Java mode [using Runtime.exec()],
 * instead simply running in the PDE process. This is mostly for simplicity,
 * especially because I'm going to be implementing an REPL later.
 * 
 * Architecture note: Processing.py has a 'PApplet Jython Driver' class that
 * contains a private Python interpreter. It injects the PApplet variables and
 * methods into this interpreter, updating whenever it needs to. It runs the
 * input python directly, without modification.
 * 
 * I'm using a different approach, similar to vanilla Processing: preprocess the
 * .pde input into a valid python class inheriting from PApplet, running the
 * resulting code through an interpreter, and extracting a the class to create a
 * final Java object to run as a PApplet.
 * 
 */

public class PythonRunner {
	PythonBuild build; // the python source we're working with
	PythonEditor editor;

	InteractiveConsole interp; // python interpreter to feed things to
								// TODO subclass?

	PythonPApplet constructedApplet;

	public PythonRunner(PythonBuild build, PythonEditor editor) {
		this.build = build;
		this.editor = editor;
	}

	/*
	 * Run the code.
	 */
	public void launch(boolean present) {
		interp = new InteractiveConsole();	// create jython environment
		interp.exec(build.getResults()); 	// execute final build string

		// TODO extract & run class in thread
	}

	/*
	 * Kill the code.
	 */
	public void close() {

	}
}
