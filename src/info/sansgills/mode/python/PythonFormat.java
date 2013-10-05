package info.sansgills.mode.python;

import processing.app.Formatter;

/**
 * 
 * Class used when the user selects the 'format' option from the menu.
 * Currently does absolutely nothing.
 * 
 */

public class PythonFormat implements Formatter {
	public PythonFormat() {}

	@Override
	public String format(String arg0) {
		return arg0; //TODO implement
	}

}
