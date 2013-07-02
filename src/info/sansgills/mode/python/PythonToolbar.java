package info.sansgills.mode.python;

import java.awt.Image;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import processing.app.Base;
import processing.app.Editor;
import processing.app.EditorToolbar;
import processing.mode.java.JavaEditor;



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
	public void handlePressed(MouseEvent e, int sel) {
		boolean shift = e.isShiftDown();
		PythonEditor peditor = (PythonEditor) editor;

		switch (sel) {
		case RUN:
			if (shift) {
				peditor.handlePresent();
			} else {
				peditor.handleRun();
			}
			break;

		case STOP:
			peditor.handleStop();
			break;

		case OPEN:
			JPopupMenu popup = editor.getMode().getToolbarMenu().getPopupMenu(); //the 'open' dropdown
			popup.show(this, e.getX(), e.getY());
			break;

		case NEW:
			base.handleNew();
			break;

		case SAVE:
			peditor.handleSave(false);
			break;

		case EXPORT:
			peditor.handleExportApplication();
			break;
		}
	}

	@Override
	public void init() { //open up the processing icons
	    Image[][] images = loadImages();
	    for (int i = 0; i < 6; i++) {
	      addButton(getTitle(i, false), getTitle(i, true), images[i], i == NEW);
	    }
	  }

}