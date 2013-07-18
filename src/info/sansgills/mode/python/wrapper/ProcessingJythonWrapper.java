package info.sansgills.mode.python.wrapper;

import java.io.File;
import java.util.Arrays;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.InteractiveConsole;

/**
 * 
 * Class to handle running jython sketches. Packaged separately from the main mode for easy export.
 * Currently run from PythonRunner.java.
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

public class ProcessingJythonWrapper {
	
	//EVERYTHING IS STATIC
	
	static InteractiveConsole interp;		// python interpreter to feed things to
	static PySystemState sys;				// for modifying python classpath, as yet
	static PythonPApplet constructedApplet;	// Applet we pull from the interpreter
	
	static final String objname = "__applet__";
	
	/**
	 * 
	 * First argument is the path to the script; the rest are things to pass to PApplet.
	 * 
	 */
	public static void main(String[] args) {
		String scriptPath = args[0];
		String[] params = Arrays.copyOfRange(args, 1, args.length);
		run(scriptPath, params);
	}

	/*
	 * Run.
	 */
	public static void run(String scriptPath, String[] params){
		interp = new InteractiveConsole();	// create jython environment
		sys = Py.getSystemState();			// python 'sys' variable
		
		
		// give jython the packages we need
		// TODO libraries?
		sys.add_package("info.sansgills.mode.python.wrapper");
		sys.add_package("processing.core");
		sys.add_package("processing.opengl");
		
		try {
			//run the script we were given; should define a PythonPApplet subclass and create an instance
			interp.execfile(scriptPath);
			
			//get the applet we constructed
			PyObject pythonApplet = interp.get(objname);
			
			if (pythonApplet != null) {
				// aaaand convert to an applet through jython magic
				constructedApplet = (PythonPApplet) pythonApplet.__tojava__(PythonPApplet.class);
			} else {
				throw new Exception("Couldn't construct applet.");
			}
			
			//run the sketch!
			PythonPApplet.runSketch(params, constructedApplet);
			
		} catch (Exception e){
			System.err.println("Error running sketch: " + e.getMessage()); //TODO
			e.printStackTrace();
			System.exit(1);
		}
	}
}
