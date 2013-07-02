package info.sansgills.mode.python;

import processing.app.Editor;
import processing.app.Preferences;
import processing.app.Sketch;
import processing.app.syntax.JEditTextArea;

import java.awt.*;
import java.awt.event.*;

/**
 * 
 * A class to handle smart-indentation and things.
 * Should really use a proper python parser, will have to find and/or make one.
 * 
 * So, the Editor class uses a homebrewed text editing panel based on the primordial goo that was to become JEdit (see jedit.org).
 * That class is hardcoded to have a PdeKeyListener managing things; to override stuff, I have to override it.
 * 
 */
public class PythonKeyListener extends processing.mode.java.PdeKeyListener{
	  
	  public PythonKeyListener(Editor editor, JEditTextArea textarea){
		  super(editor, textarea);
	  }
}
