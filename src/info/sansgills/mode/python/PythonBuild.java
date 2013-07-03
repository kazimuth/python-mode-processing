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
	String resultProgram;	//old-school
	
	//build tracking
	public int buildnumber;
	private static int buildstotal = 0;
	
	
	public PythonBuild(Sketch sketch){
		this.sketch = sketch;
		
		buildnumber = buildstotal;
		buildstotal++;
	}
	
	/*
	 * Preprocess the sketch- turn the .pde files into valid python.
	 */
	public void build() throws Exception{
		//a start
		resultProgram = "";
		
		SketchCode[] parts = sketch.getCode();		//TODO this function doesn't work properly, it returns old code for some reason
		//concatenate code strings
		for(int i = parts.length-1; i >= 0; i--){	//iterate backwards... it seemed like a good idea at the time
			resultProgram += "\n";
			resultProgram += parts[i].getProgram();
		}
		
		//System.out.println("output code: "+resultProgram);
		
		//more things?
	}
	
	/*
	 * The output code string
	 */
	public String getResults(){
		return resultProgram;
	}
	
	/*
	 * The PApplet subclass to extract from the code
	 */
	public String getClassName(){
		return "Placeholder";		//TODO implement naming scheme
	}
}
