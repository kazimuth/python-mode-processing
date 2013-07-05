package info.sansgills.mode.python.wrapper;

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
		scriptPath = args[1];
		params = Arrays.copyOfRange(args, 1, args.length);
		run();
	}

	
	public static void run(){
		interp = new InteractiveConsole();	// create jython environment
		sys = Py.getSystemState();			// python 'sys' variable
		
		//path to the archive of this wrapper, for Jython to import PythonPApplet from
		String jarPath = ""; //TODO
		
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
		}
	}
	
}
