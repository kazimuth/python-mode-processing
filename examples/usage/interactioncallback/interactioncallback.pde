def setup():
	size(200,200)

def draw():
	pass

def mouseDragged():
    # scribble a line with the mouse
    stroke(0)
    line (pmouseX, pmouseY, mouseX, mouseY)
    
def keyPressed():
    # typing 'c' clears the screen
    if key == 'c':
        background(200)

