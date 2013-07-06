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
	
	static String scriptPath;
	static String objname = "applet";
	
	static String[] params;
	
	/**
	 * 
	 * First argument is the path to the script; the rest are things to pass to PApplet.
	 * 
	 */
	public static void main(String[] args) {
		scriptPath = args[0];
		params = Arrays.copyOfRange(args, 1, args.length);
		run();
	}

	/*
	 * Run.
	 */
	public static void run(){
		interp = new InteractiveConsole();	// create jython environment
		sys = Py.getSystemState();			// python 'sys' variable
		
		//path to the archive of this wrapper, for Jython to import PythonPApplet from
		String jarPath = getJarLocation(); //TODO
		
		sys.path.append(new PyString(jarPath));
		
		
		try {
			//run the script we were given; should define a PythonPApplet subclass and create an instance
			interp.execfile(scriptPath);
			
			//get the applet we constructed
			PyObject pythonApplet = interp.get(objname);
			
			//aaaand convert to an applet through jython magic
			constructedApplet = (PythonPApplet) pythonApplet.__tojava__(PythonPApplet.class);
			
			//run the sketch!
			PythonPApplet.runSketch(params, constructedApplet);
			
		} catch (Exception e){
			System.err.println("Error running sketch: "+e.getMessage()); //TODO
			System.exit(1);
		}
	}
	
	/*
	 * An odd bit of introspection to get the location of this .jar file
	 * May not work...
	 */
	private static String getJarLocation(){
		try{
			File jar = new File(ProcessingJythonWrapper.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			return jar.getAbsolutePath();
		}catch(Exception e){
			System.err.println("Error getting .jar location: "+e.getMessage());
			System.exit(1);
		}
		return null;
	}
}
