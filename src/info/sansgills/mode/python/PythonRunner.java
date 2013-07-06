package info.sansgills.mode.python;

import java.util.ArrayList;

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
		exec(buildArgs(present));
	}
	
	/*
	 * Construct the command-line command to start the sketch with
	 */
	private String[] buildArgs(boolean present){
		ArrayList<String> args = new ArrayList<String>();
		
		args.add("java");
		args.add("");
		
		String[] out = args.toArray(new String[0]);
		System.out.println("command: "+out);
		return out;
	}
	
	private void exec(final String[] args){
		new Thread(new Runnable(){
			public void run(){
				sketchProcess = PApplet.exec(args);
				try{
					int result = sketchProcess.waitFor();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/*
	 * Kill the code.
	 */
	public void close() {
		
	}
}
