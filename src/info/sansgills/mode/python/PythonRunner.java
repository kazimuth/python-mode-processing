package info.sansgills.mode.python;

import java.io.InputStream;
import java.util.ArrayList;

import processing.app.Base;
import processing.app.exec.StreamRedirectThread;
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
	PythonEditor editor;			// our editor (TODO command line?)
	
	Process sketchProcess;			// the process we create

	// Threads to redirect output / error streams from process to us
	Thread errThread = null;
	Thread outThread = null;
	
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
	 * 
	 * TODO add proper machine detection & whatnot from Java Mode
	 */
	private String[] buildArgs(boolean present){
		ArrayList<String> args = new ArrayList<String>();
		
		args.add("java");
		args.add("-cp");
		args.add(build.getClassPath());
		
		args.add("info.sansgills.mode.python.wrapper.ProcessingJythonWrapper");	//main class
		
		args.add(build.getResultFile());	//path to script
		args.add("Placeholder");			//sketch name
		
		String[] out = args.toArray(new String[0]);
		
		{//debugging
			String cmd = "";
			for (String c : out) {
				cmd += c + " ";
			}
			System.out.println("command: " + cmd);
		}
		
		return out;
	}
	
	/*
	 * Start the process & a thread to track it
	 */
	private void exec(final String[] args){
		new Thread(new Runnable(){
			public void run(){
				sketchProcess = PApplet.exec(args);
				attach(); //TODO dirty hack
				try{
					int result = sketchProcess.waitFor();
					System.out.println("result: "+result);
				}catch(InterruptedException e){
					System.out.println("error:");
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/*
	 * Create redirect threads for stdout, stderr, etc.
	 * 
	 * TODO connect vm & error threads
	 */
	private void attach(){
		// piggybacking off of Java Mode's redirect for now
		outThread = new StreamRedirectThread("JVM stdout Reader",
				sketchProcess.getInputStream(), System.out);
		errThread = new StreamRedirectThread("JVM stderr Reader",
				sketchProcess.getErrorStream(), System.err);
		
		outThread.start();
		errThread.start();
	}
	
	/*
	 * Kill the code.
	 */
	public void close() {
		
	}
}
