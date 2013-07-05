package info.sansgills.mode.python;

import java.io.File;
import java.io.PrintWriter;

import processing.app.Sketch;
import processing.app.SketchCode;


/**
 * 
 * Class to handle the building of python files.
 * Given that python runs as an interpreter, all this does is handle preprocessing.
 * 
 */

public class PythonBuild {
	Sketch sketch;
	String resultProgram;	//old-school
	
	File binFolder;
	File outFile;
	
	//build tracking
	private int buildnumber;
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
		
		preprocess();
		
		
		//write output file
		binFolder = sketch.makeTempFolder();
		
		outFile = new File(binFolder.getAbsolutePath()
				+ sketch.getName().toLowerCase()
				+ ".py");
		outFile.createNewFile();
		PrintWriter writer = new PrintWriter(outFile);
		writer.write(resultProgram+"\n");
		writer.close();
	}
	
	/*
	 * Turn .pde into valid python
	 */
	private void preprocess(){
		//TODO implement
	}
	
	/*
	 * The output code string
	 */
	public String getResultString(){
		return resultProgram;
	}
	
	/*
	 * The output file path
	 */
	public String getResultFile(){
		return outFile.getAbsolutePath();
	}
	
	/*
	 * 
	 */
	public int getBuildNumber(){
		return buildnumber;
	}
	
	/*
	 * The PApplet subclass to extract from the code
	 */
	public String getClassName(){
		return "Placeholder";		//TODO implement naming scheme
	}
	
	/*
	 * The name of the output object
	 */
	public String getObjName(){
		return "applet";			// TODO
	}
}
