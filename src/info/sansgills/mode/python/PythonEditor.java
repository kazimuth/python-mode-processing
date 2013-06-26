package info.sansgills.mode.python;

import javax.swing.JMenu;

import processing.app.Base;
import processing.app.Editor;
import processing.app.EditorState;
import processing.app.EditorToolbar;
import processing.app.Formatter;
import processing.app.Mode;

public class PythonEditor extends Editor {

	protected PythonEditor(final Base base, String path, EditorState state, final Mode mode) {
		super(base, path, state, mode);
	}
	
	@Override
	public JMenu buildFileMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMenu buildHelpMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMenu buildSketchMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Formatter createFormatter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditorToolbar createToolbar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deactivateRun() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCommentPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleImportLibrary(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void internalCloseRunner() {
		// TODO Auto-generated method stub

	}

}
