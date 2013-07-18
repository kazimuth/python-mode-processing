package info.sansgills.mode.python;

import java.io.File;
import processing.app.*;
import processing.mode.java.JavaMode;
import processing.mode.java.runner.Runner;

/**
 * 
 * Python Mode. Yeah.
 *
 */
public class PythonMode extends Mode {
   
	public PythonMode(Base base, File folder) {
        super(base, folder);
    }

    /**
     * Create a new editor associated with this mode.
     */
    public Editor createEditor(Base base, String path, EditorState state) {
		return new PythonEditor(base, path, state, this);
    }
    
    /**
     * Return the pretty/printable/menu name for this mode. This is separate
     * from the single word name of the folder that contains this mode. It could
     * even have spaces, though that might result in sheer madness or total
     * mayhem.
     */
    public String getTitle() {
        return "Python";
    }
    
    /*
     * Utility
     */
    public static String getModeFolder(){
    	return Base.getSketchbookModesFolder()
    			+ File.separator
    			+ "PythonMode"
    			+ File.separator;
    }
    
    /**
     * Returns the default extension for this editor setup.
     * NOTE: no '.' at the beginning, that causes problems!
     */
    public String getDefaultExtension() {
        return "pde";
    }
    
    /**
     * Returns a String[] array of proper extensions.
     */
    public String[] getExtensions() {
        return new String[]{"pde", "py"};
    }
    
    /**
     * Returns the core library; in this case, a java library, because, jython.
     */
    @Override
    public Library getCoreLibrary() {
        if (coreLibrary == null) {
          File coreFolder = Base.getContentFile("core");
          coreLibrary = new Library(coreFolder);
        }
        return coreLibrary;
      }

    /**
     * Get array of file/directory names that needn't be copied during "Save
     * As".
     */
    public String[] getIgnorable() {
        return new String[]{};
    }
    
    
    /**
     * Retrieve the ClassLoader for JavaMode. This is used by Compiler to load
     * ECJ classes. Thanks to Ben Fry.
     *
     * @return the class loader from java mode
     */
    public ClassLoader getClassLoader() {
        for (Mode m : base.getModeList()) {
            if (m.getClass() == JavaMode.class) {
                JavaMode jMode = (JavaMode) m;
                return jMode.getClassLoader();
            }
        }
        return null;  // something broke
    }

}
