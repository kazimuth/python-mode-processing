RUNNING A SKETCH
what python mode actually does when you click 'run'

TEXT PROCESSING
Read in and concatenate user code
Search & replace user-readable PApplet instance variables (mouseX, mouseY, pmouseX, pmouseY, mouseButton, mousePressed, key, keyCode, keyPressed)
	with __applet__.[var]
Check if user code defines setup(); if not, surround with setup() function definition
Process user globals?
prepend prepend.py
write to file

PYTHON RUNTIME
prepend.py:
	import PApplet and PythonPApplet
	create __applet__
	ensure that __applet__.mousePressed and __applet__.keyPressed refer to variables and not methods
	inject PApplet static methods & constants into global namespace
	process PApplet instance methods into lambdas with __applet__ and inject into global namespace
	delete non-processing keys from global namespace
user code:
	define setup() (guaranteed by build)
	optionally define draw(), mouseClicked(), mouseDragged(), mouseMoved(), mousePressed(), mouseReleased(), mouseWheel(event), keyPressed(), keyReleased(), keyTyped()
		note: mousePressed() and keyPressed() will not conflict with instance variables because instances were replaced in build with __applet__.[instance]
	do whatever else they want

JAVA RUNTIME
	create python interpreter
	run built file through interpreter
	extract __applet__ variable from interpreter
	extract user-defined functions from interpreter
	run __applet__ with PApplet.runSketch(args, __applet__)