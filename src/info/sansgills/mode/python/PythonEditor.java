package info.sansgills.mode.python;

import javax.swing.JMenu;

import processing.app.Base;
import processing.app.Editor;
import processing.app.EditorState;
import processing.app.EditorToolbar;
import processing.app.Formatter;
import processing.app.Mode;

/**
 * 
 * Main editor.
 * Superclass handles most of the nitty-gritty; we just have to manage python-specific things, like running and stopping.
 *
 */

public class PythonEditor extends Editor {
	
	protected PythonEditor(final Base base, String path, EditorState state, final Mode mode) {
		super(base, path, state, mode);
	}
	
	
	
	public String getCommentPrefix() {
		return "#";
	}
	
	
	
	@Override
	public void deactivateRun() {
		//deactivate toolbar 'run' icon
	}
	public void deactivateExport() {
		//deactivate toolbar 'export' icon
	}
	
	
	
	@Override
	public void internalCloseRunner() {
		// TODO figure out what this does
	}
	
	
	/**
	 * Build menus.
	 */
	@Override
	public JMenu buildFileMenu() {
		return null;
	}
	@Override
	public JMenu buildHelpMenu() {
		return null;
	}
	@Override
	public JMenu buildSketchMenu() {
		return null;
	}

	
	
	@Override
	public Formatter createFormatter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditorToolbar createToolbar() {
		return new PythonToolbar(this, base);
	}


	/**
	 * Handlers
	 */
	public void handleExportApplication() {
		
	}
	public void handleRun() {
		
	}
	public void handlePresent() {
		
	}
	public void handleStop() {
		
	}
	public void handleSave() {
		
	}
	public boolean handleSaveAs() {
		return false;
	}
	@Override
	public void handleImportLibrary(String arg0) {
		// TODO Auto-generated method stub
	}


}
