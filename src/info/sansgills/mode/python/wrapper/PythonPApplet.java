package info.sansgills.mode.python.wrapper;

import processing.core.PApplet;

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
	
	// workaround for naming conflicts
	public boolean getMousePressed(){
		return mousePressed;
	}
	
	public boolean getKeyPressed(){
		return keyPressed;
	}
}
