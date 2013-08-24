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
 */

public class PythonRunner {
	PythonEditor editor;			// our editor

	Process sketchProcess;			// the process we create

	// Threads to redirect output / error streams from process to us
	Communicator communicator;

	boolean dying, needsReboot;

	public PythonRunner(PythonEditor editor) {
		this.editor = editor;
		dying = false;

		//make sure our partner process is dead
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				dying = true; //don't try to start again
				if (communicator != null) {
					communicator.destroy();
				}
				if (sketchProcess != null) {
					sketchProcess.destroy();
				}
			}
		}));
	}

	/*
	 * Run the code.
	 */
	public void launch(PythonBuild build, boolean present) {
		needsReboot = build.usesOpenGL;
		ensureParallel();
		String[] a = buildSketchArgs(build, present);
		communicator.sendSketch(a);
	}

	/*
	 * Kill the code.
	 */
	public void internalClose() {
		//Closed from editor button
		ensureParallel();
		communicator.sendClose();
	}

	/*
	 * Sketch process died; get ready to reboot it
	 */
	private void prepareReboot() {
		if (!dying) {
			sketchProcess = null;
			communicator.destroy();
			communicator = null;
		}
	}

	/*
	 * Force sketch process to restart
	 */
	public void forceReboot() {
		if (sketchProcess != null) sketchProcess.destroy();
	}

	/*
	 * Make sure we've got a process to run the code.
	 */
	private void ensureParallel() {
		if (sketchProcess == null) {
			sketchProcess = PApplet.exec(buildJavaArgs());
			communicator = new Communicator(sketchProcess, this);
			new Thread(new Runnable() {
				public void run() {
					try {
						int result = sketchProcess.waitFor();
						prepareReboot();
					} catch (InterruptedException e) {}
				}
			}).start();
		}
	}

	/*
	 * Handle talking to companion process
	 */
	public void parallelStopped() {
		if (needsReboot) {
			forceReboot();
		}
		editor.deactivateRun();
	}

	public void parallelStarted() {}

	public void parallelHung() {
		System.err.println("Sketch hung.");
		forceReboot();
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
			args.add("-Xdock:name=Processing"); //TODO change name each run?
		}

		//library path (for native things, etc.) and classpath
		args.add("-Djava.library.path=" + buildJavaLibraryPath());
		args.add("-cp");
		args.add(buildClassPath());

		args.add("info.sansgills.mode.python.wrapper.ProcessingJythonWrapper"); // main class

		//we parallel
		args.add("--parallel");

		return args.toArray(new String[0]);

	}

	/*
	 * Arguments for individual sketches
	 */
	private String[] buildSketchArgs(PythonBuild build, boolean present) {
		ArrayList<String> args = new ArrayList<String>();

		args.add("--script=" + build.getResultFile()); // path to script

		//the wrapper will dynamically inject these if it hasn't already
		if (build.hasJavaLibraries()) args.add("--javalibs=" + build.getJavaLibraries());
		if (build.hasPythonLibraries()) args.add("--pylibs=" + build.getPythonLibraries());

		// tell PApplet where the editor is and let it sort itself out
		Point editorLocation = editor.getLocation();
		args.add(PApplet.ARGS_EDITOR_LOCATION + "=" + editorLocation.x + "," + editorLocation.y);

		if (present) {
			args.add(PApplet.ARGS_FULL_SCREEN);
			args.add(PApplet.ARGS_STOP_COLOR + "=" + Preferences.get("run.present.stop.color"));
			args.add(PApplet.ARGS_BGCOLOR + "=" + Preferences.get("run.present.bgcolor"));
		}

		args.add(build.getName()); // sketch name MUST BE LAST

		return args.toArray(new String[0]);
	}

	/*
	 * Moving this here from PythonBuild because it's generic (i.e. not
	 * build-dependent) We can send new libraries dynamically, as builds demand
	 * them
	 */
	private String buildClassPath() {
		// the Processing classpath
		String classPath = editor.getMode().getCoreLibrary().getClassPath();

		// From JavaMode.java:
		// Finally, add the regular Java CLASSPATH. This contains everything
		// imported by the PDE itself (core.jar, pde.jar, quaqua.jar) which may
		// in fact be more of a problem.
		String javaClassPath = System.getProperty("java.class.path");
		// Remove quotes if any.. A messy (and frequent) Windows problem
		if (javaClassPath.startsWith("\"") && javaClassPath.endsWith("\"")) {
			javaClassPath = javaClassPath.substring(1, javaClassPath.length() - 1);
		}
		classPath += File.pathSeparator + javaClassPath;

		// The .jars for this mode; Jython and wrapper
		String jythonModeLib = Base.getSketchbookModesFolder().getAbsolutePath() + File.separator + "PythonMode" + File.separator + "mode";
		classPath += Base.contentsToClassPath(new File(jythonModeLib));

		return classPath;
	}

	/*
	 * TODO figure out if I need anything else here
	 */
	private String buildJavaLibraryPath() {
		return System.getProperty("java.library.path");
	}
}
