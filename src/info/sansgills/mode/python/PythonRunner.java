package info.sansgills.mode.python;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import processing.app.Base;
import processing.app.Preferences;
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
	Communicator communicator;
	
	boolean active;
	
	public PythonRunner(PythonEditor editor) {
		this.editor = editor;
		active = false;
		
		//make sure our partner process is dead
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				if (communicator != null) {
					communicator.destroy();
				}
				if(sketchProcess != null) {
					sketchProcess.destroy();
				}
			}
		}));
	}
	
	/*
	 * Run the code.
	 */
	public void launch(PythonBuild build, boolean present) {
		this.build = build;
		ensureParallel();
		communicator.sendSketch(buildSketchArgs(present));
	}

	/*
	 * Kill the code.
	 */
	public void internalClose() {
		//Closed from editor button
		ensureParallel();
		System.out.println("Closing...");
		communicator.sendClose();
	}
	
	/*
	 * Make sure we've got a process to run the code.
	 */
	private void ensureParallel(){
		if(sketchProcess == null){
			if(build == null){
				System.err.println("need a build");
				return;
			}
			sketchProcess = PApplet.exec(buildJavaArgs());
			communicator = new Communicator(sketchProcess, this);
		}
	}
	
	
	public void parallelStopped() {
		//Closed from sketch window
		if(active){
			active = false;
			System.out.println("Closed");
		}else{
			System.err.println("something is wrong");
		}
	}
	public void parallelStarted(){
		if(!active){
			active = true;
		}else{
			System.err.println("something is very wrong");
		}
	}
	
	/*
	 * Command to start the companion process
	 */
	private String[] buildJavaArgs() {
		ArrayList<String> args = new ArrayList<String>();

		// Manage java
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
		args.add("-Djava.library.path=" + build.getJavaLibraryPath()
				+ File.pathSeparator + System.getProperty("java.library.path"));
		
		// Manage Python Mode
		args.add("-cp");
		args.add(build.getClassPath());

		args.add("info.sansgills.mode.python.wrapper.ProcessingJythonWrapper"); // main class
		
		//we parallel
		args.add("--parallel");

		return args.toArray(new String[0]);

	}
	
	/*
	 * Arguments for individual sketches
	 */
	private String[] buildSketchArgs(boolean present) {
		ArrayList<String> args = new ArrayList<String>();

		args.add("--script="+build.getResultFile()); // path to script

		// tell PApplet where the editor is and let it sort itself out
		Point editorLocation = editor.getLocation();
		args.add(PApplet.ARGS_EDITOR_LOCATION + "=" + editorLocation.x + ","
				+ editorLocation.y);

		if (present) {
			args.add(PApplet.ARGS_FULL_SCREEN);
			args.add(PApplet.ARGS_STOP_COLOR + "="
					+ Preferences.get("run.present.stop.color"));
			args.add(PApplet.ARGS_BGCOLOR + "="
					+ Preferences.get("run.present.bgcolor"));
		}

		args.add(build.getClassName()); // sketch name MUST BE LAST

		return args.toArray(new String[0]);
	}
}
