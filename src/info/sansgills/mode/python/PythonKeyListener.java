package info.sansgills.mode.python;

import processing.app.Editor;
import processing.app.Preferences;
import processing.app.Sketch;
import processing.app.syntax.JEditTextArea;

import java.awt.*;
import java.awt.event.*;

/**
 * 
 * A class to handle smart-indentation and things. Should really use a proper
 * python parser, will have to find and/or make one.
 * 
 * So, the Editor class uses a homebrewed text editing panel based on the
 * primordial goo that was to become JEdit (see jedit.org). That class is
 * hardcoded to have a PdeKeyListener managing things; to override stuff, I have
 * to override it.
 * 
 */
public class PythonKeyListener extends processing.mode.java.PdeKeyListener {
	PythonEditor peditor;
	JEditTextArea ptextarea;

	
	//ctrl-alt on windows & linux, cmd-alt on os x
	private static int CTRL_ALT = ActionEvent.ALT_MASK
			| Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); 
	
	
	public PythonKeyListener(Editor editor, JEditTextArea textarea) {
		super(editor, textarea);
		
		peditor = (PythonEditor) editor;
		ptextarea = textarea;
	}
	
	/*
	 * Handles special stuff for Java brace indenting & outdenting, etc.
	 * Overriding it 'cause we do things different here in python-land
	 * 
	 * TODO use actual parser; handle spaces
	 * 
	 * @return true if we've handled things correctly
	 */
	@Override
	public boolean keyPressed(KeyEvent event){
		char c = event.getKeyChar();
		int code = event.getKeyCode();
		
		Sketch sketch = peditor.getSketch();
		
		// things that change the content of the text area
		if ((code == KeyEvent.VK_BACK_SPACE) || (code == KeyEvent.VK_TAB)
				|| (code == KeyEvent.VK_ENTER) || ((c >= 32) && (c < 128))) {
			sketch.setModified(true);
		}
		
		// ctrl-alt-[arrow] switches sketch tab
		if ((event.getModifiers() & CTRL_ALT) == CTRL_ALT) {
			if (code == KeyEvent.VK_LEFT) {
				sketch.handlePrevCode();
				return true;
			} else if (code == KeyEvent.VK_RIGHT) {
				sketch.handleNextCode();
				return true;
			}
		}
		
		//TODO handle ctrl-[up|down]; should move cursor to next empty line in that direction
		
		// handle specific keypresses
		switch (c) {

		case 9: //tab; may do something here later. NOT overriding with spaces- this is python!
			ptextarea.setSelectedText("\t");
			break;

		case 10: //return
		case 13: //also return
			char[] text = ptextarea.getText().toCharArray();	//text
			int cursorLocation = ptextarea.getCaretPosition();	//location of element to be placed; may be out of bounds
			
			int tabs = getTabCount(cursorLocation, text);
						
			String insert = "\n";
			for(int i=0; i<tabs; i++){
				insert += "\t";
			}
			
			ptextarea.setSelectedText(insert);
			break;
		}
		
		
		
		return false;
	}
	
	//given an index and a block of text, find the number of tabs in the line containing the index
	//TODO dirty hack
	int getTabCount(int index, char[] text){
		int prev = index-1; //the start of the line
		
		
		//walk till we find the beginning of the line (or text)
		while(prev >= 0 && text[prev] != '\n'){
			prev--;
		}
		//prev is now at the previous newline
		prev++;
		
		//prev is now at the beginning of the line
		//walk forward, counting tabs
		int tabCount = 0;
		while(prev < text.length && text[prev] == '\t'){
			tabCount++;
			prev++;
		}
		
		return tabCount;
	}
	
}
