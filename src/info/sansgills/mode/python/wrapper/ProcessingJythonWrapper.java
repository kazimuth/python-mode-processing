package info.sansgills.mode.python.wrapper;

import info.sansgills.mode.python.PythonEditor;

import java.awt.GraphicsDevice;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

import org.python.core.*;
import org.python.util.InteractiveConsole;

import processing.core.PApplet;

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
	//Read in some scripts from the jar (this is the only one-liner to read in a stream, don't you love java)
	static final String prepend;
	static final String scrub;
	static{
		prepend = new Scanner(ProcessingJythonWrapper.class.getResourceAsStream("prepend.py")).useDelimiter("\\A").next();
		scrub = new Scanner(ProcessingJythonWrapper.class.getResourceAsStream("scrub.py")).useDelimiter("\\A").next();
	}
	
	static final String[] sketchFunctions = { "setup", "draw", "mousePressed",
			"mouseReleased", "mouseClicked", "mouseWheel", "mouseMoved",
			"mouseDragged", "keyPressed", "keyReleased", "keyTyped" };
	
	static MessageReceiverThread receiver;
	
	static InteractiveConsole interp;		// python interpreter to feed things to
	static PySystemState sys;				// for modifying python classpath, as yet
	static PythonPApplet constructedApplet;	// Applet we pull from the interpreter
	
	static final String objname = "__applet__";
	
	static boolean parallel;
	
	/**
	 * 
	 * First argument is the path to the script; the rest are things to pass to
	 * PApplet.
	 * 
	 */
	public static void main(String[] args) {
		if (args[0].indexOf("__PARALLEL__") != -1) {
			parallel = true;
			receiver = new MessageReceiverThread(System.in);
			receiver.start();
			// running from PDE
		} else {
			parallel = false;
			String scriptPath = args[0];
			String[] params = Arrays.copyOfRange(args, 1, args.length);
			receiver = new MessageReceiverThread(System.in);
			receiver.start();

			prepare();
			run(scriptPath, params);
		}
	}

	public static void prepare() {
		interp = new InteractiveConsole(); // create jython environment
		sys = Py.getSystemState(); // python 'sys' variable

		// give jython the packages we need
		// TODO libraries?
		PySystemState.add_package("info.sansgills.mode.python.wrapper");
		PySystemState.add_package("processing.core");
		PySystemState.add_package("processing.opengl");
	}
	
	/*
	 * Called by PythonPApplet when it's exiting
	 * Get rid of all references to our applet
	 * 
	 * ...this tangle of callbacks probably makes me a bad person
	 */
	static void sketchExiting(){
		if (parallel) {
			scrub();
			constructedApplet = null;
			sys = null;
		} else {
			System.exit(0);
		}
	}

	/*
	 * Run.
	 */
	public static void run(String scriptPath, String[] params){
		try {
			//run prepend.py
			interp.exec(prepend);
			
			//run the script we were given
			interp.execfile(scriptPath);
			
			//get the applet we constructed
			PyObject pythonApplet = interp.get(objname);
			
			if (pythonApplet != null) {
				// aaaand convert to an applet through jython magic
				constructedApplet = (PythonPApplet) pythonApplet.__tojava__(PythonPApplet.class);
			} else {
				throw new Exception("Couldn't construct applet.");
			}
			
			//go through functions the sketch might have defined and give them to the applet
			for (String name : sketchFunctions){
				PyFunction function = (PyFunction) interp.get(name);
				if(function != null){
					constructedApplet.inject(name, function);
				}
			}
			
			//run the sketch!
			PythonPApplet.runSketchOpen(params, constructedApplet);
			
		} catch (Exception e){
			System.err.println("Error running sketch: " + e.getMessage()); //TODO
			e.printStackTrace();
			if(constructedApplet != null){
				constructedApplet.exit();
			}
		}
	}
	
	public static void scrub(){
		interp.exec(scrub);
	}
	
	
	
	/*
	 * Class to handle receiving messages from the PDE
	 * not to be confused with Communicator.MessageReceiverThread; same idea, different location
	 */
	private static class MessageReceiverThread extends Thread{
		BufferedReader messageReader;
		public boolean running;
		
		public MessageReceiverThread(InputStream messageStream){
			this.messageReader = new BufferedReader(new InputStreamReader(messageStream));
			this.running = true;
		}
		
		public void run() {
			try {
				String currentLine;

				// continually read messages
				while ((currentLine = messageReader.readLine()) != null && running) {
					try {
						if (currentLine.indexOf("__STOP__") != -1) {
							// PDE is telling us to kill the applet
							if (constructedApplet != null) {
								constructedApplet.exit();
							}
						}
					} catch (Exception e) {}
				}
			} catch (Exception e) {}
		}
	}
}
