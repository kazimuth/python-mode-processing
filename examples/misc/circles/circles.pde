def setup():
	size(300,300, P3D)
	hint(DISABLE_DEPTH_TEST)
	smooth()
	global l
	l = []

def mousePressed():
    l.append ([mouseX, mouseY, 1])
    
def mouseDragged():
    l.append ([mouseX, mouseY,1])
    
def draw():
    fill(0,10)
    noStroke()
    rect(0,0,width,height)
    stroke(255)
    nl = l[:]
    l[:]=[]
    for x,y,w in nl:
        gs = 255-w
        if gs<0: continue
        stroke(gs)
        ellipse(x,y,w,w)
        l.append([x,y,w+2])
        

