package info.sansgills.mode.python;

import processing.app.Editor;
import processing.app.Preferences;
import processing.app.Sketch;
import processing.app.syntax.JEditTextArea;

import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			String text = ptextarea.getText();	//text
			int cursor = ptextarea.getCaretPosition();	//location of element to be placed; may be out of bounds
			
			ptextarea.setSelectedText(getIndent(cursor, text));
			break;
		}
		
		
		
		return false;
	}
	
	private static Pattern findIndent = Pattern.compile("^((?: |\\t)*)");
	private static Pattern incIndent = Pattern.compile(":( |\\t)*(#.*)?$"); //TODO fix; breaks on strings (":#"\n) and so on
	
	String getIndent(int cursor, String text){
		if(cursor <= 1) return "\n";
		
		int lineStart, lineEnd;
		int i;
		for(i = cursor - 1; i>=0 && text.charAt(i)!='\n'; i--);
		lineStart = i+1;
		for(i = cursor - 1; i<text.length() && text.charAt(i)!='\n' ; i++);
		lineEnd = i;
		
		if(lineEnd <= lineStart) return "\n";
		
		String line = text.substring(lineStart, lineEnd);
		
		String indent;
		Matcher f = findIndent.matcher(line);
		
		if(f.find()){
			indent ='\n' + f.group();
			
			if (incIndent.matcher(line).find()){
				indent += '\t';
			}
		}else{
			indent = "\n";
		}
		
		return indent;
	}
}
