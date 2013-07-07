Processing Python Mode
----------------------

[Processing](http://www.processing.org/) is awesome! [Jython](http://www.jython.org/) is awesome! Together, they are TOTALLY AWESOME!

This project uses Processing 2.0's mode infrastructure to create a seamless blend of Processing and python.

Inspired by the handsome [processing.py](https://github.com/jdf/processing.py).

This is very much a work-in-progress. Don't expect it to work very well right now.

To install: unzip PythonMode.zip into "{your sketchfolder}/modes/PythonMode" and restart Processing.

Check build.xml for building instructions.

Currently working on:
- Basic functionality- running python code

Future work:
- Preprocessor
- Autoindent & syntax highlighting
- REPL for live coding

As I don't have the preprocessor done, code isn't very pretty right now, but it works!
A working sketch (copy and paste into the PDE to try it out!):

	import info.sansgills.mode.python.wrapper.PythonPApplet as PApplet
	
	class Placeholder(PApplet):
		def setup(self):
			self.size(200, 200)
			self.background(0)
			self.noStroke()
			self.ellipseMode(PApplet.CENTER)
	
		def draw(self):
			self.ellipse(self.mouseX, self.mouseY, 5, 5)
	
	applet = Placeholder()