"""
Retained textured cube processing.py 
using processing-2.0 PShape
by Martin Prout
"""

def setup():
  global tex, texturedCube
  size(640, 480, P3D)
  tex = loadImage("data/tex.jpg")
  textureMode(NORMAL)
  fill(255)
  stroke(color(44,48,32))  
  texturedCube = TexturedCube(tex)

def draw():
  background(0)
  noStroke()    
  translate(width/2, height/2)
  texturedCube.rotateX(PI/250)   
  texturedCube.rotateZ(PI/200) 
  scale(120)
  shape(texturedCube)  

def TexturedCube(tex):
  shape = createShape(PShape.QUADS)
  shape.texture(tex)
  shape.vertex(-1, -1,  1, 0, 0)
  shape.vertex( 1, -1,  1, 1, 0)
  shape.vertex( 1,  1,  1, 1, 1)
  shape.vertex(-1,  1,  1, 0, 1)
  shape.vertex( 1, -1, -1, 0, 0)
  shape.vertex(-1, -1, -1, 1, 0)
  shape.vertex(-1,  1, -1, 1, 1)
  shape.vertex( 1,  1, -1, 0, 1)
  shape.vertex(-1,  1,  1, 0, 0)
  shape.vertex( 1,  1,  1, 1, 0)
  shape.vertex( 1,  1, -1, 1, 1)
  shape.vertex(-1,  1, -1, 0, 1)
  shape.vertex(-1, -1, -1, 0, 0)
  shape.vertex( 1, -1, -1, 1, 0)
  shape.vertex( 1, -1,  1, 1, 1)
  shape.vertex(-1, -1,  1, 0, 1)
  shape.vertex( 1, -1,  1, 0, 0)
  shape.vertex( 1, -1, -1, 1, 0)
  shape.vertex( 1,  1, -1, 1, 1)
  shape.vertex( 1,  1,  1, 0, 1)
  shape.vertex(-1, -1, -1, 0, 0)
  shape.vertex(-1, -1,  1, 1, 0)
  shape.vertex(-1,  1,  1, 1, 1)
  shape.vertex(-1,  1, -1, 0, 1)
  shape.end()
  return(shape) 

