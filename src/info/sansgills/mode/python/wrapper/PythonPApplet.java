package info.sansgills.mode.python.wrapper;

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
}
