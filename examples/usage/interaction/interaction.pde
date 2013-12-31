def setup():
	size(200,200)

def draw():
    # scribble a line with the mouse
    if mousePressed:
        line (pmouseX, pmouseY, mouseX, mouseY)
    # typing 'c' clears the screen
    if keyPressed and key == 'c':
        background(200)


