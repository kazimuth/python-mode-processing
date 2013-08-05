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
import processing.app.Toolkit;
import processing.mode.java.JavaEditor;
import processing.mode.java.JavaToolbar;

/**
 * 
 * Main editor.
 * Superclass handles most of the nitty-gritty; we just have to manage python-specific things, like running and stopping.
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
	public void deactivateRun() {		//deactivate toolbar 'run' icon
		toolbar.deactivate(PythonToolbar.RUN);
	}
	public void deactivateExport() {	//deactivate toolbar 'export' icon
		toolbar.deactivate(PythonToolbar.EXPORT);
	}
	
	
	
	@Override
	public void internalCloseRunner() {
		handleStop(); //Java Mode does this, copying on faith
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
		new Thread(new Runnable(){
			public void run(){
				toolbar.activate(PythonToolbar.RUN);			//pretty lights
				prepareRun();
				PythonBuild build = new PythonBuild(sketch, pyMode);	//create build
				try {
					build.build();								//run build
				} catch (Exception e){
					statusError(e);								//do something pretty?
				}
				runner.launch(build, false);							//launch runtime; present = false
			}
		}).start();
	}

	public void handlePresent() {
		new Thread(new Runnable() {
			public void run() {
				toolbar.activate(PythonToolbar.RUN);
				prepareRun();
				PythonBuild build = new PythonBuild(sketch, pyMode);
				try {
					build.build();
				} catch (Exception e){
					statusError(e);
				}
				runner.launch(build, true);							//present = true
			}
		}).start();
	}
	
	public void handleStop() { //copied wholesale from Java Mode
		toolbar.activate(PythonToolbar.STOP);
		try {
	      runner.internalClose();
	    } catch (Exception e) {
	      statusError(e);
	    }
	    toolbar.deactivate(PythonToolbar.RUN);
	    toolbar.deactivate(PythonToolbar.STOP);

	    // focus the PDE again after quitting presentation mode [toxi 030903]
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
	public void statusError(String what){ //sketch died for some reason
		super.statusError(what);
		deactivateRun();
	}
}
