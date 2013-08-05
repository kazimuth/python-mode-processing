package info.sansgills.mode.python.wrapper;

import info.sansgills.mode.python.PythonEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	static String prepend;
	static String scrub;
	
	static final String[] sketchFunctions = { "setup", "draw", "mousePressed",
			"mouseReleased", "mouseClicked", "mouseWheel", "mouseMoved",
			"mouseDragged", "keyPressed", "keyReleased", "keyTyped" };
	
	static MessageReceiverThread receiver;
	
	static InteractiveConsole interp;		// python interpreter to feed things to
	static PySystemState sys;				// for modifying python classpath, as yet
	static PythonPApplet applet;	// Applet we pull from the interpreter
	
	static final String objname = "__applet__";
	
	static boolean parallel;
	static boolean ready;
	
	/*
	 * Starting up our process; either it's running in parallel with an editor,
	 * in which case we get ready to connect and pass messages, or it's not, in
	 * which case this is a one-shot job and nobody's listening
	 */
	public static void main(String[] args) {
		ready = false;
		
		// the PDE will have this as the only arument if we're running paired
		parallel = args[0].indexOf("--parallel") != -1;
		
		if (parallel) { //running from the PDE
			receiver = new MessageReceiverThread(System.in);
			receiver.start();
			prepare();
			ready = true;
		} else {		//on our own
			prepare();
			runSketch(args);
		}
	}
	
	/*
	 * Things we need to run only once.
	 */
	public static void prepare() {
		prepend = new Scanner(ProcessingJythonWrapper.class.getResourceAsStream("prepend.py")).useDelimiter("\\A").next();
		scrub = new Scanner(ProcessingJythonWrapper.class.getResourceAsStream("scrub.py")).useDelimiter("\\A").next();
		
		if (PythonPApplet.platform == PythonPApplet.MACOSX) {
			System.setProperty("apple.awt.graphics.UseQuartz",
					String.valueOf(PythonPApplet.useQuartz));
		}

		// Doesn't seem to do much to help avoid flicker
		System.setProperty("sun.awt.noerasebackground", "true");

		interp = new InteractiveConsole(); // create jython environment
		sys = Py.getSystemState(); // python 'sys' variable
		
		PySystemState.add_package("info.sansgills.mode.python.wrapper");
		PySystemState.add_package("processing.core");
		PySystemState.add_package("processing.opengl");
	}
	
	public static void startSketch(String[] args){
		if(applet != null){
			applet.exit();
			
			while(applet != null){
				try{
					Thread.sleep(50);
				}catch(InterruptedException e){}
			}
		}
		//applet is now null
		//python context is scrubbed
		//we're good to go, hopefully
		
		runSketch(args);
		
	}
	
	
	/*
	 * 
	 */
	public static void constructApplet(String scriptPath){
		while (!ready) {
			try {
				Thread.sleep(50);
			} catch (Exception e){}
		}
		try {
			// run prepend.py
			interp.exec(prepend);

			// run the script we were given
			interp.execfile(scriptPath);

			// get the applet we constructed
			PyObject pythonApplet = interp.get(objname);

			//make sure we've got something
			if (pythonApplet == null) throw new Exception("Couldn't construct applet.");
			
			// aaaand convert to an applet through jython magic
			applet = (PythonPApplet) pythonApplet.__tojava__(PythonPApplet.class);

			// go through functions the sketch might have defined and
			// give them to the applet
			for (String name : sketchFunctions) {
				PyFunction function = (PyFunction) interp.get(name);
				if (function != null) {
					applet.inject(name, function);
				}
			}
			
			//done!
		} catch (Exception e) {
			System.err.println("Error running sketch: " + e.getMessage()); // TODO do some proper parsing
			e.printStackTrace();
			if (applet != null) {
				applet.exit();
			}
		}
	}
	
	
	/*
	 * We've been told to kill the sketch by an external source
	 * Calling the exit function and letting it sort itself out
	 */
	public static void terminateSketch(){
		if (applet != null){
			applet.exit();
		}
	}
	
	/*
	 * Sketch has released all its resources; we're forgetting about it
	 * Hopefully this doesn't leak...
	 */
	public static void sketchDisposed(){
		scrubContext();
		applet = null; //all done
	}
	
	
	/*
	 * Clean up our python context.
	 */
	public static void scrubContext(){
		interp.exec(scrub);
	}
	
	/*
	 *  A monolithic method to make a sketch go.
	 */
	public static void runSketch(final String args[]) {
		if (args.length < 1) {
			System.err.println("Usage: PApplet <appletname>");
			System.err.println("For additional options, see the Javadoc for PApplet");
			return;
		}

		int[] location = null;
		int[] editorLocation = null;
		
		String scriptPath = null;

		String name = null;
		boolean present = false;
		Color backgroundColor = null; 
		Color stopColor = Color.GRAY;
		GraphicsDevice displayDevice = null;
		boolean hideStop = false;

		String param = null, value = null;

		// try to get the user folder. if running under java web start,
		// this may cause a security exception if the code is not signed.
		// http://processing.org/discourse/yabb_beta/YaBB.cgi?board=Integrate;action=display;num=1159386274
		String folder = null;
		try {
			folder = System.getProperty("user.dir");
		} catch (Exception e) {}

		int argIndex = 0;
		while (argIndex < args.length) {
			int equals = args[argIndex].indexOf('=');
			if (equals != -1) {
				param = args[argIndex].substring(0, equals);
				value = args[argIndex].substring(equals + 1);

				if (param.equals(PythonPApplet.ARGS_EDITOR_LOCATION)) {
					editorLocation = PythonPApplet.parseInt(PythonPApplet.split(value, ','));
				} else if (param.equals(PythonPApplet.ARGS_DISPLAY)) {
					int deviceIndex = Integer.parseInt(value);
					GraphicsEnvironment environment = GraphicsEnvironment
							.getLocalGraphicsEnvironment();
					GraphicsDevice devices[] = environment.getScreenDevices();
					if ((deviceIndex >= 0) && (deviceIndex < devices.length)) {
						displayDevice = devices[deviceIndex];
					} else {
						System.err.println("Display " + value
								+ " does not exist, "
								+ "using the default display instead.");
						for (int i = 0; i < devices.length; i++) {
							System.err.println(i + " is " + devices[i]);
						}
					}
				} else if (param.equals(PythonPApplet.ARGS_BGCOLOR)) {
					if (value.charAt(0) == '#')
						value = value.substring(1);
					backgroundColor = new Color(Integer.parseInt(value, 16));
				} else if (param.equals(PythonPApplet.ARGS_STOP_COLOR)) {
					if (value.charAt(0) == '#')
						value = value.substring(1);
					stopColor = new Color(Integer.parseInt(value, 16));
				} else if (param.equals(PythonPApplet.ARGS_SKETCH_FOLDER)) {
					folder = value;
				} else if (param.equals(PythonPApplet.ARGS_LOCATION)) {
					location = PythonPApplet.parseInt(PythonPApplet.split(value, ','));
				} else if (param.equals(PythonPApplet.ARGS_SCRIPT)) {
					scriptPath = value;
				}

			} else {
				if (args[argIndex].equals(PythonPApplet.ARGS_PRESENT)) { // keep for compatability
					present = true;

				} else if (args[argIndex].equals(PythonPApplet.ARGS_FULL_SCREEN)) {
					present = true;

				} else if (args[argIndex].equals(PythonPApplet.ARGS_HIDE_STOP)) {
					hideStop = true;

				} else {
					name = args[argIndex];
					break; // because of break, argIndex won't increment again
				}
			}
			argIndex++;
		}

		// Now that sketch path is passed in args after the sketch name
		// it's not set in the above loop(the above loop breaks after
		// finding sketch name). So setting sketch path here.
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith(PythonPApplet.ARGS_SKETCH_FOLDER)) {
				folder = args[i].substring(args[i].indexOf('=') + 1);
			}
		}

		if (displayDevice == null) {
			GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			displayDevice = environment.getDefaultScreenDevice();
		}

		Frame frame = new Frame(displayDevice.getDefaultConfiguration());
		frame.setBackground(new Color(0xCC, 0xCC, 0xCC)); // default Processing gray
		
		constructApplet(scriptPath);
		
		if (applet == null) {
			System.err.println("Where's the applet?");
			return;
		}

		frame.setTitle(name);

		// A handful of things that need to be set before init/start.
		applet.sketchPath = folder;
		// Query the applet to see if it wants to be full screen all the time.
		present |= applet.sketchFullScreen();
		// pass everything after the class name in as args to the sketch itself
		// (fixed for 2.0a5, this was just subsetting by 1, which didn't skip
		// opts)
		applet.args = PApplet.subset(args, argIndex + 1);
		
		applet.external = parallel;

		// Need to save the window bounds at full screen,
		// because pack() will cause the bounds to go to zero.
		// http://dev.processing.org/bugs/show_bug.cgi?id=923
		Rectangle screenRect = displayDevice.getDefaultConfiguration()
				.getBounds();
		// DisplayMode doesn't work here, because we can't get the upper-left
		// corner of the display, which is important for multi-display setups.

		// Sketch has already requested to be the same as the screen's
		// width and height, so let's roll with full screen mode.
		if (screenRect.width == applet.sketchWidth() && screenRect.height == applet.sketchHeight()) {
			present = true;
		}

		// For 0149, moving this code (up to the pack() method) before init().
		// For OpenGL (and perhaps other renderers in the future), a peer is
		// needed before a GLDrawable can be created. So pack() needs to be
		// called on the Frame before applet.init(), which itself calls size(),
		// and launches the Thread that will kick off setup().
		// http://dev.processing.org/bugs/show_bug.cgi?id=891
		// http://dev.processing.org/bugs/show_bug.cgi?id=908
		if (present) {
			frame.setUndecorated(true);
			if (backgroundColor != null) {
				frame.setBackground(backgroundColor);
			}
			frame.setBounds(screenRect);
			frame.setVisible(true);
		}
		applet.connectFrame(frame);
		if (present) {
			frame.invalidate();
		} else {
			frame.pack();
		}

		// disabling resize has to happen after pack() to avoid apparent Apple bug
		// http://code.google.com/p/processing/issues/detail?id=467
		frame.setResizable(false);

		applet.init();

		// Wait until the applet has figured out its width.
		// In a static mode app, this will be after setup() has completed,
		// and the empty draw() has set "finished" to true.
		// TODO make sure this won't hang if the applet has an exception.
		while (applet.defaultSize && !applet.finished) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {}
		}

		if (present) {
			if (PythonPApplet.platform == PythonPApplet.MACOSX) {
				// Call some native code to remove the menu bar on OS X. Not necessary
				// on Linux and Windows, who are happy to make full screen windows.
				japplemenubar.JAppleMenuBar.hide();
			}

			// After the pack(), the screen bounds are gonna be 0s
			frame.setBounds(screenRect);
			applet.setBounds((screenRect.width - applet.width) / 2,
					(screenRect.height - applet.height) / 2, applet.width,
					applet.height);

			if (!hideStop) {
				Label label = new Label("stop");
				label.setForeground(stopColor);
				label.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(java.awt.event.MouseEvent e) {
						applet.exit();
					}
				});
				frame.add(label);

				Dimension labelSize = label.getPreferredSize();
				labelSize = new Dimension(100, labelSize.height);
				label.setSize(labelSize);
				label.setLocation(20, screenRect.height - labelSize.height - 20);
			}

			// not always running externally when in present mode
			if (parallel) {
				applet.setupExternalMessages();
			}

		} else { // if not presenting
			// can't do pack earlier cuz present mode don't like it
			// (can't go full screen with a frame after calling pack)
			Insets insets = frame.getInsets();

			int windowW = Math.max(applet.width, PythonPApplet.MIN_WINDOW_WIDTH)
					+ insets.left + insets.right;
			int windowH = Math.max(applet.height, PythonPApplet.MIN_WINDOW_HEIGHT)
					+ insets.top + insets.bottom;

			frame.setSize(windowW, windowH);

			if (location != null) {
				// a specific location was received from the Runner
				// (applet has been run more than once, user placed window)
				frame.setLocation(location[0], location[1]);

			} else if (parallel && editorLocation != null) {
				int locationX = editorLocation[0] - 20;
				int locationY = editorLocation[1];

				if (locationX - windowW > 10) {
					// if it fits to the left of the window
					frame.setLocation(locationX - windowW, locationY);

				} else { // doesn't fit
					// if it fits inside the editor window,
					// offset slightly from upper lefthand corner
					// so that it's plunked inside the text area
					locationX = editorLocation[0] + 66;
					locationY = editorLocation[1] + 66;

					if ((locationX + windowW > applet.displayWidth - 33)
							|| (locationY + windowH > applet.displayHeight - 33)) {
						// otherwise center on screen
						locationX = (applet.displayWidth - windowW) / 2;
						locationY = (applet.displayHeight - windowH) / 2;
					}
					frame.setLocation(locationX, locationY);
				}
			} else { // just center on screen
				// Can't use frame.setLocationRelativeTo(null) because it sends
				// the
				// frame to the main display, which undermines the --display
				// setting.
				frame.setLocation(screenRect.x
						+ (screenRect.width - applet.width) / 2, screenRect.y
						+ (screenRect.height - applet.height) / 2);
			}
			Point frameLoc = frame.getLocation();
			if (frameLoc.y < 0) {
				// Windows actually allows you to place frames where they can't be closed. Awesome.
				// http://dev.processing.org/bugs/show_bug.cgi?id=1508
				frame.setLocation(frameLoc.x, 30);
			}

			if (backgroundColor != null) {
				frame.setBackground(backgroundColor);
			}

			int usableWindowH = windowH - insets.top - insets.bottom;
			applet.setBounds((windowW - applet.width) / 2, insets.top
					+ (usableWindowH - applet.height) / 2, applet.width,
					applet.height);
			
			// send messages back to the PDE
			if (parallel) {
				setupExternalMessages(frame);
			}
			// handle frame resizing events
			applet.setupFrameResizeListener();

			// all set for rockin
			if (applet.displayable()) {
				frame.setVisible(true);
			}
		}
	}
	
	/*
	 * 
	 */
	public static void setupExternalMessages(Frame frame) {
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//exit method handles sending messages; we're done here
				terminateSketch();
			}
		});
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
							terminateSketch();
						}else if (currentLine.indexOf("__SKETCH__") != -1){
							startSketch(PythonPApplet.subset(currentLine.split(" "), 1));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
