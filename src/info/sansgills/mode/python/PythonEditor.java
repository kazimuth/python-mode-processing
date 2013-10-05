package info.sansgills.mode.python;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import processing.app.Base;
import processing.app.Editor;
import processing.app.EditorState;
import processing.app.EditorToolbar;
import processing.app.Formatter;
import processing.app.Mode;
import processing.app.Preferences;
import processing.app.Toolkit;
import processing.mode.java.JavaEditor;
import processing.mode.java.JavaToolbar;

/**
 * 
 * The main editor class. Unlike in Java Mode the editor also handles running and exporting code,
 * because I'm lazy.
 * 
 * Does basically what you'd expect it to.
 * 
 */
@SuppressWarnings("serial")
public class PythonEditor extends Editor {

	PythonMode pyMode;
	PythonKeyListener listener; //handles syntax highlighting / indents via black magic

	private PythonRunner runner;

	protected PythonEditor(final Base base, String path, EditorState state, final Mode mode) {
		super(base, path, state, mode);

		runner = new PythonRunner(this);

		listener = new PythonKeyListener(this, textarea); //black magic
		pyMode = (PythonMode) mode; //convenience
	}

	public String getCommentPrefix() {
		return "#";
	}

	@Override
	public void internalCloseRunner() {
		try {
			runner.internalClose();
		} catch (Exception e) {
			statusError(e);
		}
	}

	/**
	 * Build menus.
	 */
	@Override
	public JMenu buildFileMenu() {
		//Okay, this is kinda weird
		String appTitle = PythonToolbar.getTitle(PythonToolbar.EXPORT, false);  //get export string

		JMenuItem exportApplication = Toolkit.newJMenuItem(appTitle, 'E'); //set it up

		exportApplication.addActionListener(new ActionListener() { //yadda yadda
			public void actionPerformed(ActionEvent e) {
				handleExportApplication();
			}
		});
		return buildFileMenu(new JMenuItem[] { exportApplication }); //and then call the SUPERCLASS method
	}

	@Override
	public JMenu buildHelpMenu() { //TODO implement
		JMenu menu = new JMenu("Help");
		JMenuItem item = new JMenuItem("help is for weaklings");
		item.setEnabled(false);
		menu.add(item);
		return menu;
	}

	@Override
	public JMenu buildSketchMenu() { //the 'Sketch' menu, if that wasn't obvious
		JMenuItem runItem = Toolkit.newJMenuItem(PythonToolbar.getTitle(PythonToolbar.RUN, false), 'R');
		runItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleRun();
			}
		});

		JMenuItem presentItem = Toolkit.newJMenuItemShift(PythonToolbar.getTitle(PythonToolbar.RUN, true), 'R');
		presentItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handlePresent();
			}
		});

		JMenuItem stopItem = new JMenuItem(PythonToolbar.getTitle(PythonToolbar.STOP, false));
		stopItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleStop();
			}
		});

		return buildSketchMenu(new JMenuItem[] { runItem, presentItem, stopItem });
	}

	@Override
	public Formatter createFormatter() {
		return new PythonFormat();
	}

	@Override
	public EditorToolbar createToolbar() {
		return new PythonToolbar(this, base);
	}

	/**
	 * Handlers
	 */
	public void handleExportApplication() {
		Base.showMessage("Sorry", "You can't do that yet."); //TODO implement
	}

	//Note that I'm doing the build here instead of in PythonMode, because of simplicity
	public void handleRun() {
		toolbar.activate(PythonToolbar.RUN);
		new Thread(new Runnable() {
			public void run() {
				prepareRun();
				PythonBuild build = new PythonBuild(sketch, pyMode);	//create build
				try {
					build.build();								//run build
					runner.launch(build, false);				//launch runtime; present = false
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}

	public void handlePresent() {
		toolbar.activate(PythonToolbar.RUN);
		new Thread(new Runnable() {
			public void run() {
				prepareRun();
				PythonBuild build = new PythonBuild(sketch, pyMode);
				try {
					build.build();
					runner.launch(build, true);					//present = true
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}

	public void handleStop() {
		toolbar.activate(PythonToolbar.STOP);
		internalCloseRunner();
		toolbar.deactivate(PythonToolbar.STOP);
		toolbar.deactivate(PythonToolbar.RUN);
		toFront();
	}

	public void handleSave() {
		toolbar.activate(PythonToolbar.SAVE);
		super.handleSave(true);
		toolbar.deactivate(PythonToolbar.SAVE);
	}

	public boolean handleSaveAs() {
		toolbar.activate(PythonToolbar.SAVE);
		boolean result = super.handleSaveAs();
		toolbar.deactivate(PythonToolbar.SAVE);
		return result;
	}

	@Override
	public void handleImportLibrary(String arg0) {
		Base.showMessage("Sorry", "You can't do that yet."); //TODO implement
	}

	@Override
	public void statusError(String what) { //sketch died for some reason
		super.statusError(what);
		toolbar.deactivate(PythonToolbar.RUN);
	}
	
	@Override
	public void deactivateRun(){
		toolbar.deactivate(PythonToolbar.RUN);
	}
}
