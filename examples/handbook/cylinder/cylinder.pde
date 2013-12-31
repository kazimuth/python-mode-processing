# Draw a cylinder centered on the y-axis, going down from y=0 to y=height.
# The radius at the top can be different from the radius at the bottom,
# and the number of sides drawn is variable.



def setup() :
  size(400, 400, P3D)
  global my_shape
  my_shape = drawCylinder(10, 180, 200, 16) # Draw a mix between a cylinder and a cone
  #my_shape = drawCylinder(70, 70, 120, 16) # Draw a cylinder
  #my_shape = drawCylinder(0, 180, 200, 4) # Draw a pyramid
  

def draw():
  background(0)
  lights()
  translate(width / 2, height / 2)
  rotateY(mouseX * PI / width)
  rotateZ(mouseY * -PI / height)
  
  fill(255, 255, 255)
  translate(0, -40, 0)
  shape(my_shape)

def drawCylinder(topRadius, bottomRadius, tall, sides):
  noStroke()
  cone = createShape(PShape.GROUP)
  angle = 0
  angleIncrement = TWO_PI / sides
  cylinder = createShape(PShape.QUAD_STRIP)
  for i in range(sides+1):
    #normal(cos(angle),sin(angle),0)
    cylinder.vertex(topRadius*cos(angle), 0, topRadius*sin(angle))
    cylinder.vertex(bottomRadius*cos(angle), tall, bottomRadius*sin(angle))
    angle += angleIncrement
  cylinder.end()
  cone.addChild(cylinder)
  # If it is not a cone, draw the circular top cap
  if (topRadius != 0):
    angle = 0
    top = createShape(PShape.TRIANGLE_FAN)
    
    # Center point
    top.vertex(0, 0, 0)
    for i in range(sides+1):
      top.vertex(topRadius * cos(angle), 0, topRadius * sin(angle))
      angle += angleIncrement
    top.end()
    cone.addChild(top)
  # If it is not a cone, draw the circular bottom cap
  if (bottomRadius != 0):
    angle = 0
    bottom = createShape(PShape.TRIANGLE_FAN)

    # Center point
    bottom.vertex(0, tall, 0)
    for i in range(sides+1):
      bottom.vertex(bottomRadius * cos(angle), tall, bottomRadius * sin(angle))
      angle += angleIncrement
    
    bottom.end()
    return cone


