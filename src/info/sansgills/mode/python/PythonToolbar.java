package info.sansgills.mode.python;

import java.awt.Image;
import java.awt.event.MouseEvent;

import processing.app.Base;
import processing.app.Editor;
import processing.app.EditorToolbar;



public class PythonToolbar extends EditorToolbar {
	
	//The row of icons at the top of the PDE.
	//Handles images, titles, and clicking events.

	static protected final int RUN 		= 0;
	static protected final int STOP 	= 1;

	static protected final int NEW		= 2;
	static protected final int OPEN		= 3;
	static protected final int SAVE 	= 4;
	static protected final int EXPORT 	= 5;

	static public String getTitle(int index, boolean shift) {
		switch (index) {
			case RUN:		return !shift ? "Run" : "Present";
			case STOP:		return "Stop";
			case NEW:		return "New";
			case OPEN:		return "Open";
			case SAVE:		return "Save";
			case EXPORT:	return "Export Application";
		}
		return null;
	}
	
	public PythonToolbar(Editor editor, Base base) {
		super(editor, base);
	}

	@Override
	public void handlePressed(MouseEvent arg0, int arg1) {
		Base.showMessage("Sorry", "You can't do that yet."); //TODO implement
	}

	@Override
	public void init() { //open up the processing icons
	    Image[][] images = loadImages();
	    for (int i = 0; i < 6; i++) {
	      addButton(getTitle(i, false), getTitle(i, true), images[i], i == NEW);
	    }
	  }

}