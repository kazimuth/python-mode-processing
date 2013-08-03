package info.sansgills.mode.python.wrapper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import processing.core.PApplet;
import processing.event.MouseEvent;

import org.python.core.*;

/**
 * 
 * Class for python applets.
 * Instead of overriding, they inject functions into it and it runs them.
 * 
 * Also provides information for creation of PApplet globals.
 *
 */

public class PythonPApplet extends PApplet {
	
	//Accessed from prepend.py
	public static final String[] staticMethods = { "abs", "acos", "append",
			"arrayCopy", "asin", "atan", "atan2", "binary", "blendColor",
			"ceil", "concat", "constrain", "cos", "createInput",
			"createOutput", "createReader", "createWriter", "day", "debug",
			"degrees", "dist", "exec", "exp", "expand", "floor", "hex", "hour",
			"join", "lerp", "lerpColor", "loadBytes", "loadStrings", "log",
			"mag", "map", "match", "matchAll", "max", "min",
			"minute", "month", "nf", "nfc", "nfp", "nfs", "norm", "open",
			"pow", "print", "println", "radians", "reverse", "round",
			"saveBytes", "saveStream", "saveStrings", "second", "shorten",
			"sin", "sort", "splice", "split", "splitTokens", "sq", "sqrt",
			"subset", "tan", "trim", "unbinary", "unhex", "year" };
	
	public static final String[] constants = { "ADD", "ALPHA", "ALT",
			"AMBIENT", "ARC", "ARGB", "ARROW", "BACKSPACE", "BASELINE",
			"BEVEL", "BEZIER_VERTEX", "BLEND", "BLUR", "BOTTOM", "BOX",
			"BREAK", "BURN", "CENTER", "CHATTER", "CHORD", "CLAMP", "CLOSE",
			"CODED", "COMPLAINT", "CONTROL", "CORNER", "CORNERS", "CROSS",
			"CURVE_VERTEX", "CUSTOM", "DARKEST", "DEG_TO_RAD", "DELETE",
			"DIAMETER", "DIFFERENCE", "DILATE", "DIRECTIONAL",
			"DISABLE_DEPTH_MASK", "DISABLE_DEPTH_SORT", "DISABLE_DEPTH_TEST",
			"DISABLE_NATIVE_FONTS", "DISABLE_OPENGL_ERRORS",
			"DISABLE_OPTIMIZED_STROKE", "DISABLE_RETINA_PIXELS",
			"DISABLE_STROKE_PERSPECTIVE", "DISABLE_STROKE_PURE",
			"DISABLE_TEXTURE_MIPMAPS", "DODGE", "DOWN", "DXF", "ELLIPSE",
			"ENABLE_DEPTH_MASK", "ENABLE_DEPTH_SORT", "ENABLE_DEPTH_TEST",
			"ENABLE_NATIVE_FONTS", "ENABLE_OPENGL_ERRORS",
			"ENABLE_OPTIMIZED_STROKE", "ENABLE_RETINA_PIXELS",
			"ENABLE_STROKE_PERSPECTIVE", "ENABLE_STROKE_PURE",
			"ENABLE_TEXTURE_MIPMAPS", "ENTER", "EPSILON", "ERODE",
			"ERROR_BACKGROUND_IMAGE_FORMAT", "ERROR_BACKGROUND_IMAGE_SIZE",
			"ERROR_PUSHMATRIX_OVERFLOW", "ERROR_PUSHMATRIX_UNDERFLOW",
			"ERROR_TEXTFONT_NULL_PFONT", "ESC", "EXCLUSION", "GIF", "GRAY",
			"GROUP", "HALF_PI", "HAND", "HARD_LIGHT", "HINT_COUNT", "HSB",
			"IMAGE", "INVERT", "JAVA2D", "JPEG", "LANDSCAPE", "LEFT",
			"LIGHTEST", "LINE", "LINE_LOOP", "LINE_STRIP", "LINES", "LINUX",
			"MACOSX", "MAX_FLOAT", "MAX_INT", "MIN_FLOAT", "MIN_INT", "MITER",
			"MODEL", "MODELVIEW", "MOVE", "MULTIPLY", "NORMAL", "OPAQUE",
			"OPEN", "OPENGL", "ORTHOGRAPHIC", "OTHER", "OVERLAY", "P2D", "P3D",
			"PATH", "PDF", "PERSPECTIVE", "PI", "PIE", "platformNames",
			"POINT", "POINTS", "POLYGON", "PORTRAIT", "POSTERIZE", "PROBLEM",
			"PROJECT", "PROJECTION", "QUAD", "QUAD_BEZIER_VERTEX",
			"QUAD_STRIP", "QUADS", "QUARTER_PI", "RAD_TO_DEG", "RADIUS",
			"RECT", "REPEAT", "REPLACE", "RETURN", "RGB", "RIGHT", "ROUND",
			"SCREEN", "SHAPE", "SHIFT", "SOFT_LIGHT", "SPHERE", "SPOT",
			"SQUARE", "SUBTRACT", "TAB", "TARGA", "TAU", "TEXT", "THIRD_PI",
			"THRESHOLD", "TIFF", "TOP", "TRIANGLE", "TRIANGLE_FAN",
			"TRIANGLE_STRIP", "TRIANGLES", "TWO_PI", "UP", "VERTEX", "WAIT",
			"WHITESPACE", "WINDOWS", "X", "Y", "Z" };
	
	
	private HashMap<String, PyFunction> sketchFunctions;	
	
	public PythonPApplet(){
		super();
		
		sketchFunctions = new HashMap<String, PyFunction>();
	}
	
	public void inject(String name, PyFunction function){
		sketchFunctions.put(name, function);
	}
	
	//woo
	
	@Override
	public void setup(){
		try{
			if(sketchFunctions.containsKey("setup")) sketchFunctions.get("setup").__call__();
		}catch(PyException e){
			if(e.getCause() instanceof RendererChangeException){
				// Processing uses this to signal a change in renderer
				// chuck it up the stack
				throw (RendererChangeException) e.getCause();
			}
		}
	}
	@Override
	public void draw(){
		if(sketchFunctions.containsKey("draw")) sketchFunctions.get("draw").__call__();
	}
	@Override
	public void mousePressed(){
		if(sketchFunctions.containsKey("mousePressed")) sketchFunctions.get("mousePressed").__call__();
	}
	@Override
	public void mouseReleased(){
		if(sketchFunctions.containsKey("mouseReleased")) sketchFunctions.get("mouseReleased").__call__();
	}
	@Override
	public void mouseClicked(){
		if(sketchFunctions.containsKey("mouseClicked")) sketchFunctions.get("mouseClicked").__call__();
	}
	@Override
	public void mouseMoved(){
		if(sketchFunctions.containsKey("mouseMoved")) sketchFunctions.get("mouseMoved").__call__();
	}
	@Override
	public void mouseDragged(){
		if(sketchFunctions.containsKey("mouseDragged")) sketchFunctions.get("mouseDragged").__call__();
	}
	@Override
	public void mouseWheel(){ //TODO fix
		if(sketchFunctions.containsKey("mouseWheel")) sketchFunctions.get("mouseWheel").__call__();
	}
	@Override
	public void keyPressed(){
		if(sketchFunctions.containsKey("keyPressed")) sketchFunctions.get("keyPressed").__call__();
	}
	@Override
	public void keyReleased(){
		if(sketchFunctions.containsKey("keyReleased")) sketchFunctions.get("keyReleased").__call__();
	}
	@Override
	public void keyTyped(){
		if(sketchFunctions.containsKey("keyTyped")) sketchFunctions.get("keyTyped").__call__();
	}
	
	
	// workaround for naming conflicts
	// not properly camelcased because regex, and noone is going to see it anyway
	public boolean getmousePressed(){
		return mousePressed;
	}
	
	public boolean getkeyPressed(){
		return keyPressed;
	}
	
	public float getframeRate(){
		return frameRate;
	}
	
	
	// special code to avoid killing the process when we close an applet
	
	boolean external;

	@Override
	public void exit(){
		if(external){
			// tell the PDE we're exiting
			System.err.println(PApplet.EXTERNAL_STOP);
			System.err.flush();
		}
		
		dispose();
		frame.dispose();
		
		ProcessingJythonWrapper.sketchExiting();
	}
	
	/*
	 * Add some things to tell the PDE when things happen
	 */
	@Override
	public void setupExternalMessages() {
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				Point where = ((Frame) e.getSource()).getLocation();
				System.err.println(PApplet.EXTERNAL_MOVE+" "+where.x+" "+where.y);
				System.err.flush(); // doesn't seem to help or hurt
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//exit method handles sending messages; we're done here
				exit();
			}
		});
	}
		
	/*
	 *  overriding PApplet's runSketch to avoid having it kill the process when it's done
	 *  mostly copy-pasted from PApplet.java
	 */
	public static void runSketchOpen(final String args[], final PythonPApplet constructedApplet) {
		// Disable abyssmally slow Sun renderer on OS X 10.5.
		if (platform == MACOSX) {
			// Only run this on OS X otherwise it can cause a permissions error.
			// http://dev.processing.org/bugs/show_bug.cgi?id=976
			System.setProperty("apple.awt.graphics.UseQuartz",
					String.valueOf(useQuartz));
		}

		// Doesn't seem to do much to help avoid flicker
		System.setProperty("sun.awt.noerasebackground", "true");


		if (args.length < 1) {
			System.err.println("Usage: PApplet <appletname>");
			System.err.println("For additional options, "
					+ "see the Javadoc for PApplet");
			ProcessingJythonWrapper.sketchExiting();
			return;
		}

		boolean external = false;
		int[] location = null;
		int[] editorLocation = null;

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

				if (param.equals(ARGS_EDITOR_LOCATION)) {
					external = true;
					editorLocation = parseInt(split(value, ','));

				} else if (param.equals(ARGS_DISPLAY)) {
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

				} else if (param.equals(ARGS_BGCOLOR)) {
					if (value.charAt(0) == '#')
						value = value.substring(1);
					backgroundColor = new Color(Integer.parseInt(value, 16));

				} else if (param.equals(ARGS_STOP_COLOR)) {
					if (value.charAt(0) == '#')
						value = value.substring(1);
					stopColor = new Color(Integer.parseInt(value, 16));

				} else if (param.equals(ARGS_SKETCH_FOLDER)) {
					folder = value;

				} else if (param.equals(ARGS_LOCATION)) {
					location = parseInt(split(value, ','));
				}

			} else {
				if (args[argIndex].equals(ARGS_PRESENT)) { // keep for compatability
					present = true;

				} else if (args[argIndex].equals(ARGS_FULL_SCREEN)) {
					present = true;

				} else if (args[argIndex].equals(ARGS_HIDE_STOP)) {
					hideStop = true;

				} else if (args[argIndex].equals(ARGS_EXTERNAL)) {
					external = true;

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
			if (args[i].startsWith(ARGS_SKETCH_FOLDER)) {
				folder = args[i].substring(args[i].indexOf('=') + 1);
			}
		}

		if (displayDevice == null) {
			GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			displayDevice = environment.getDefaultScreenDevice();
		}

		Frame frame = new Frame(displayDevice.getDefaultConfiguration());
		frame.setBackground(new Color(0xCC, 0xCC, 0xCC)); // default Processing gray

		final PythonPApplet applet;
		if (constructedApplet != null) {
			applet = constructedApplet;
		} else {
			System.err.println("Where's the applet?");
			return;
		}

		// Set the trimmings around the image
		applet.setIconImage(frame);
		frame.setTitle(name);

		// A handful of things that need to be set before init/start.
		applet.frame = frame;
		applet.sketchPath = folder;
		// Query the applet to see if it wants to be full screen all the time.
		present |= applet.sketchFullScreen();
		// pass everything after the class name in as args to the sketch itself
		// (fixed for 2.0a5, this was just subsetting by 1, which didn't skip
		// opts)
		applet.args = PApplet.subset(args, argIndex + 1);
		
		applet.external = external;

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
		frame.setLayout(null);
		frame.add(applet);
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
			if (platform == MACOSX) {
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
			if (external) {
				applet.setupExternalMessages();
			}

		} else { // if not presenting
			// can't do pack earlier cuz present mode don't like it
			// (can't go full screen with a frame after calling pack)
			Insets insets = frame.getInsets();

			int windowW = Math.max(applet.width, MIN_WINDOW_WIDTH)
					+ insets.left + insets.right;
			int windowH = Math.max(applet.height, MIN_WINDOW_HEIGHT)
					+ insets.top + insets.bottom;

			frame.setSize(windowW, windowH);

			if (location != null) {
				// a specific location was received from the Runner
				// (applet has been run more than once, user placed window)
				frame.setLocation(location[0], location[1]);

			} else if (external && editorLocation != null) {
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
			if (external) {
				applet.setupExternalMessages();
			}
			// handle frame resizing events
			applet.setupFrameResizeListener();

			// all set for rockin
			if (applet.displayable()) {
				frame.setVisible(true);
			}
		}
	}
}
