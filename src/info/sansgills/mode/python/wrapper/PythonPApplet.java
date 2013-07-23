package info.sansgills.mode.python.wrapper;

import java.util.HashMap;

import processing.core.PApplet;
import processing.event.MouseEvent;

import org.python.core.*;

/**
 * 
 * Skeleton class to give applets something to inherit from.
 * May contain special things later.
 *
 */

// TODO fix keyPressed & other constants, not working right now (if keyPressed:
// is always true and if keyPressed == True: is always false)
// TODO this looks like naming conflict problems? print keyPressed returns a method...
public class PythonPApplet extends PApplet {
	
	//Accessed from prepend.py
	public static String[] staticMethods = { "abs", "acos", "append",
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
	public static String[] instanceMethods = { "alpha", "ambient",
			"ambientLight", "applyMatrix", "arc", "background", "beginCamera",
			"beginContour", "beginRaw", "beginRecord", "beginShape", "bezier",
			"bezierDetail", "bezierPoint", "bezierTangent", "bezierVertex",
			"blend", "blendMode", "blue", "box", "brightness", "camera",
			"clear", "color", "colorMode", "copy", "createFont",
			"createGraphics", "createImage", "createInput", "createOutput",
			"createReader", "createShape", "createWriter", "cursor", "curve",
			"curveDetail", "curvePoint", "curveTangent", "curveTightness",
			"curveVertex", "directionalLight", "ellipse",
			"ellipseMode", "emissive", "endCamera", "endContour", "endRaw",
			"endRecord", "endShape", "exit", "fill", "filter", "frameRate",
			"frustum", "get", "hint", "hue", "image", "imageMode", "lerpColor",
			"lightFalloff", "lightSpecular", "lights", "line", "loadBytes",
			"loadFont", "loadImage", "loadJSONArray", "loadJSONObject",
			"loadPixels", "loadShader", "loadShape", "loadStrings",
			"loadTable", "loadXML", "loop", "millis", "modelX", "modelY",
			"modelZ", "noCursor",
			"noFill", "noLights", "noLoop", "noSmooth", "noStroke", "noTint",
			"noise", "noiseDetail", "noiseSeed", "normal", "ortho", "parseXML",
			"perspective", "point", "pointLight", "popMatrix", "popStyle",
			"printCamera", "printMatrix", "printProjection", "pushMatrix",
			"pushStyle", "quad", "quadraticVertex", "random", "randomGaussian",
			"randomSeed", "rect", "rectMode", "red", "redraw", "requestImage",
			"resetMatrix", "resetShader", "rotate", "rotateX", "rotateY",
			"saturation", "save", "saveBytes", "saveFrame", "saveJSONArray",
			"saveJSONObject", "saveStream", "saveStrings", "saveTable",
			"saveXML", "scale", "screenX", "screenY", "screenZ",
			"selectFolder", "selectInput", "selectOutput", "shader", "shape",
			"shapeMode", "shearX", "shearY", "shininess", "size", "smooth",
			"specular", "sphere", "sphereDetail", "spotLight", "stroke",
			"strokeCap", "strokeJoin", "strokeWeight", "text", "textAlign",
			"textAscent", "textDescent", "textFont", "textLeading", "textMode",
			"textSize", "textWidth", "texture", "textureMode", "tint",
			"translate", "triangle", "updatePixels", "vertex" };	
	
	
	
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
		if(sketchFunctions.containsKey("setup")) sketchFunctions.get("setup").__call__();
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
}
