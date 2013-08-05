Processing Python Mode
----------------------

[Processing](http://www.processing.org/) is awesome! [Jython](http://www.jython.org/) is awesome! Together, they are TOTALLY AWESOME!

This project uses Processing 2.0's mode infrastructure to create a seamless blend of Processing and python.

Inspired by the handsome [processing.py](https://github.com/jdf/processing.py).

This is very much a work-in-progress. Don't expect it to work very well right now.

To install: unzip PythonMode.zip into "{your sketch folder}/modes/PythonMode" and restart Processing.

Check build.xml for building instructions.

Done:
- Basic functionality- running python code
- Basic indentation & highlighting
- Basic preprocessor

Currently working on:
- Proper parser for the preprocessor

Future work:
- Library imports and sketch exports
- Better autoindent & syntax highlighting
- REPL for live coding

A working sketch (copy and paste into the PDE to try it out!):
	
	def setup():
		size(300, 300)
		smooth()
		stroke(255)
		background(0)
	
	def draw():
		line(mouseX+random(-40, 40), mouseY+random(-40, 40), mouseX, mouseY)
	
	def keyPressed():
		background(0)