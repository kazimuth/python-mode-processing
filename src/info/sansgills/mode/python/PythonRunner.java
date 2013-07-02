package info.sansgills.mode.python;

import org.python.core.*;
import org.python.util.InteractiveConsole;

/**
 * 
 * Class to handle the running of sketches.
 * Note that, for now, this does not run the sketch in a new process like in Java mode [using Runtime.exec()],
 * instead simply running in the PDE process. 
 * This is mostly for simplicity, especially because I'm going to be implementing an REPL later.
 *
 */

public class PythonRunner {
	PythonBuild build; 			//the python source we're working with
	PythonEditor editor;		//probably a bad idea, but easy to use.
	
	InteractiveConsole interp;	//python interpreter to feed things to
								//TODO subclass?
	
	public PythonRunner(PythonBuild build, PythonEditor editor){
		this.build = build;
		this.editor = editor;
	}
	
	
	/*
	 * Run the code.
	 */
	public void launch(){
		
	}
	
	/*
	 * Kill the code.
	 */
	public void close(){
		
	}
}
