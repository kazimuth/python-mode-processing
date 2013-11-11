def setup():
	size(200,200)
	global poly
	poly = []
	smooth()

def mousePressed():
    poly [:] = [(mouseX,mouseY)]

def mouseDragged():
    poly.append((mouseX,mouseY))
    
def draw():
    background(200)
    beginShape()
    for x,y in poly: vertex(x,y)
    endShape(CLOSE)



