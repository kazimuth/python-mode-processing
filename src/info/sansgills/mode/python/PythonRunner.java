package info.sansgills.mode.python;

import org.python.core.*;
import org.python.util.InteractiveConsole;

import processing.app.Base;

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
 * .pde input into a valid python class inheriting from PApplet, run the
 * resulting code through an interpreter, and extract the class to create a
 * final Java object to run as a PApplet.
 * 
 */

public class PythonRunner {
	PythonBuild build;				// the python source we're working with
	PythonEditor editor;
	PythonPApplet constructedApplet;

	InteractiveConsole interp;		// python interpreter to feed things to
									// TODO subclass?
	PySystemState sys;				// for modifying python classpath, as yet
	
	
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
		interp = new InteractiveConsole();	// create jython environment
		sys = Py.getSystemState();			// python 'sys' variable
		
		//path to the archive of this mode, for jython to import the PythonPApplet class from
		//for me, "C:\Dev\Processing\modes\PythonMode\mode\PythonMode.jar"
		String jarPath = Base.getSketchbookModesFolder() +
				modeName +
				sep +
				"mode" +
				sep +
				modeName +
				".jar";
		
		sys.path.append(new PyString(jarPath));
		
		
		interp.exec(build.getResults()); 	// execute final build string
		String className = build.getClassName();
		try {
			PyType pythonClass = (PyType) interp.get(className);	//get the user-made class
			PyObject pythonApplet = pythonClass.__call__();			//make an object out of it
			
			//aaaand convert to an applet through jython magic
			constructedApplet = (PythonPApplet) pythonApplet.__tojava__(PythonPApplet.class);
			
			//placeholder for properly running the sketch; this'll do for now
			PythonPApplet.runSketch(new String[]{className}, constructedApplet);	//TODO implement properly
		} catch (Exception e){
			System.err.println("Could not extract class "+className+": "+e.getMessage());
		}
	}

	/*
	 * Kill the code.
	 */
	public void close() {
		System.out.println("closing interpreter");
		if(interp != null){
			interp = null;
			//not sure what else to do here
		}
	}
}
