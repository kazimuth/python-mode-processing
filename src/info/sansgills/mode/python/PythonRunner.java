package info.sansgills.mode.python;

import java.awt.Point;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import processing.app.Base;
import processing.app.Preferences;
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
		
		// Manage java
		appendJavaArgs(args);
		
		// Manage Python Mode
		args.add("-cp");
		args.add(build.getClassPath());

		args.add("info.sansgills.mode.python.wrapper.ProcessingJythonWrapper"); // main class
		
		args.add(build.getResultFile());	//path to script
		
		appendSketchArgs(args, present);
				
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
	 * Create the proper args to run the jvm with
	 */
	private void appendJavaArgs(ArrayList<String> args) {

		// special handling for base command for OS X- from Java Mode
		if (!Base.isMacOS()) {
			args.add("java");
		} else {
			args.add("/usr/libexec/java_home");
			if (System.getProperty("os.version").startsWith("10.6")) {
				args.add("--request"); // install the correct jvm, if we don't have it
			}
			args.add("--version");
			args.add("1.6");
			args.add("--exec");
			args.add("java");
			args.add("-d" + Base.getNativeBits());
		}

		// Special options
		String options = Preferences.get("run.options");
		if (options.length() > 0) {
			String pieces[] = PApplet.split(options, ' ');
			for (int i = 0; i < pieces.length; i++) {
				String p = pieces[i].trim();
				if (p.length() > 0) {
					args.add(p);
				}
			}
		}
		
		// Memory
		if (Preferences.getBoolean("run.options.memory")) {
			args.add("-Xms" + Preferences.get("run.options.memory.initial") + "m");
			args.add("-Xmx" + Preferences.get("run.options.memory.maximum") + "m");
		}

		// Pretty app name on OS X
		if (Base.isMacOS()) {
			args.add("-Xdock:name=" + build.getClassName());
		}
		
		// Path to the libraryies we use
		// TODO what's the difference between classpath and library path?
		args.add("-Djava.library.path=" 
				+ build.getJavaLibraryPath()
				+ File.pathSeparator 
				+ System.getProperty("java.library.path"));

	}
	
	/*
	 * Sketch-specific stuff
	 */
	private void appendSketchArgs(ArrayList<String> args, boolean present){
		// place the sketch
		// TODO handle multiple displays
		Point windowLocation = editor.getSketchLocation();
		if (windowLocation != null) {
			// saved location - sketch run more than once
			args.add(PApplet.ARGS_LOCATION 
					+ "=" 
					+ windowLocation.x + "," + windowLocation.y);
		} else {
			// tell PApplet where the editor is and let it sort itself out
			Point editorLocation = editor.getLocation();
			args.add(PApplet.ARGS_EDITOR_LOCATION 
					+ "=" 
					+ editorLocation.x + "," + editorLocation.y);
		}

		if (present) {
			args.add(PApplet.ARGS_FULL_SCREEN);
			args.add(PApplet.ARGS_STOP_COLOR + "=" + Preferences.get("run.present.stop.color"));
			args.add(PApplet.ARGS_BGCOLOR + "=" + Preferences.get("run.present.bgcolor"));
		}

		args.add(build.getClassName()); // sketch name
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
		if(sketchProcess != null){
			sketchProcess.destroy();
			sketchProcess = null;
		}
	}
}
