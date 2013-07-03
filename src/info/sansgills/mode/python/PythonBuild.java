package info.sansgills.mode.python;

import processing.app.Sketch;
import processing.app.SketchCode;
import org.python.util.*;
import org.python.core.*;


/**
 * 
 * Class to handle the building of python files.
 * Given that python runs as an interpreter, all this does is handle preprocessing.
 * 
 */

public class PythonBuild {
	Sketch sketch;
	
	public PythonBuild(Sketch sketch){
		this.sketch = sketch;
	}
	
	/*
	 * Preprocess the sketch- turn the .pde files into valid python.
	 */
	public void build() throws Exception{ //TODO make specific exception class? whatever.
		
	}
	
	/*
	 * The output code string
	 */
	public String getResults(){
		return "print \"hello\"";	//TODO implement
	}
}
