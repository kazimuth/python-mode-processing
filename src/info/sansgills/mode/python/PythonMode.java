package info.sansgills.mode.python;

import java.io.File;
import processing.app.Base;
import processing.app.Editor;
import processing.app.EditorState;
import processing.app.Mode;
import processing.mode.java.JavaMode;

/**
 * Python Mode. Yeah.
 *
 */
public class PythonMode extends Mode {
    public PythonMode(Base base, File folder) {
        super(base, folder);
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

    /**
     * Create a new editor associated with this mode.
     */
    public Editor createEditor(Base base, String path, EditorState state) {
		return null;
    }
    

    /**
     * Returns the default extension for this editor setup.
     */
    public String getDefaultExtension() {
        return ".pde";
    }
    

    /**
     * Returns a String[] array of proper extensions.
     */
    public String[] getExtensions() {
        return new String[]{".pde", ".py"};
    }
    

    /**
     * Get array of file/directory names that needn't be copied during "Save
     * As".
     */
    public String[] getIgnorable() {
        return null;
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
