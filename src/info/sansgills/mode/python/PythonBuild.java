package info.sansgills.mode.python;

import java.io.File;
import java.io.PrintWriter;

import processing.app.Base;
import processing.app.Library;
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
	
	PythonMode mode;
	
	File binFolder;
	File outFile;
	
	//build tracking
	private int buildnumber;
	private static int buildstotal = 0;
	
	
	public PythonBuild(Sketch sketch, PythonMode mode){
		this.sketch = sketch;
		this.mode = mode;
		
		buildnumber = buildstotal;
		buildstotal++;
	}
	
	/*
	 * Preprocess the sketch- turn the .pde files into valid python.
	 */
	public void build() throws Exception{
		
		resultProgram = "";
		
		SketchCode[] parts = sketch.getCode(); //fix'd
		
		//concatenate code strings
		for(int i = parts.length-1; i >= 0; i--){	//iterate backwards... it seemed like a good idea at the time
			resultProgram += "\n";
			resultProgram += parts[i].getProgram();
		}
		
		preprocess();
		
		
		//write output file
		binFolder = sketch.makeTempFolder();
		
		outFile = new File(binFolder.getAbsolutePath()
				+ File.separator
				+ sketch.getName().toLowerCase()
				+ ".py");
		outFile.createNewFile();
	
		
		PrintWriter writer = new PrintWriter(outFile);
		writer.print(resultProgram+"\n");
		writer.close();
	}
	
	/*
	 * Turn .pde into valid python
	 */
	private void preprocess(){

		// Load a small python file that handles imports & some python-specific
		// stuff (overriding some PVector methods, for example)
		try{
			String core = Base.loadFile(new File(mode.getModeFolder()+"core.py"));
			resultProgram = core + "\n" + resultProgram;
		}catch(Exception e){
			System.out.println("Preprocessing failed.");
		}
		
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
	 * Classes used to run the build.
	 * 
	 * TODO build during preprocess
	 */
	public String getClassPath() {
		//the Processing classpath
		String cp = mode.getCoreLibrary().getClassPath();
		
		// From JavaMode.java:
		// Finally, add the regular Java CLASSPATH. This contains everything
		// imported by the PDE itself (core.jar, pde.jar, quaqua.jar) which may
		// in fact be more of a problem.
		String javaClassPath = System.getProperty("java.class.path");
		// Remove quotes if any.. A messy (and frequent) Windows problem
		if (javaClassPath.startsWith("\"") && javaClassPath.endsWith("\"")) {
			javaClassPath = javaClassPath.substring(1,
					javaClassPath.length() - 1);
		}
		cp += File.pathSeparator + javaClassPath;
		
		//The .jars for this mode; Jython and wrapper, primarily
		String jythonModeLib = Base.getSketchbookModesFolder().getAbsolutePath()
				+ File.separator
				+ "PythonMode"
				+ File.separator
				+ "mode";
		System.out.println(jythonModeLib);
		cp += Base.contentsToClassPath(new File(jythonModeLib));
		
		
		return cp; // TODO libraries?
	}

	public String getJavaLibraryPath(){
		return "";
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
