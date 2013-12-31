# Draw a sphere on top of a box and moves the coordinates with the mouse
# Press a mouse button to turn on the lights
def setup():
  size(400, 400, P3D)

def draw():
  background(0);
  if mousePressed == True: 
    # If the mouse is pressed,
    lights(); # turn on lights
  noStroke()
  pushMatrix()
  translate(mouseX, mouseY, -500)
  rotateY(PI / 6) # Rotate around y-axis
  box(400, 100, 400) # Draw box
  pushMatrix()
  popMatrix()
  translate(0, -200, 0) # Position the sphere
  sphere(150) # Draw sphere on top of box
  popMatrix()


