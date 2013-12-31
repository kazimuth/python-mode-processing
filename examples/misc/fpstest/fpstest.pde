from random import randint
 
# Choose the total number of bouncing balls
nballs = 150
balls = [()]*nballs

def setup():
    size(400,400)
    noStroke()
    ellipseMode(CENTER)
    # generate n balls with random positions, sizes, speeds and colors
    for i in range(nballs):
        # position
        x, y = randint(20, 380), randint(20, 380)
        # speed
        dx, dy = randint(1, 4), randint(-4, -1)
        # radius
        r = randint(10, 20)
        # color
        c = color(randint(10, 255),randint(10, 255),randint(10, 255))  
        balls[i] = (x,y,dx,dy,r,c)

        
def draw():
    # fade the last frame by drawing background in transparent color
    fill(255,50)
    rect(0,0,width,height)
    # draw/bounce balls
    for i, ball in enumerate(balls):
		x,y,dx,dy,r,c = ball
		fill(c)
		x += dx
		y += dy
		diam = r/2
		if constrain(x, diam, 400 - diam) != x:
			dx *= -1    	       	
		if constrain(y, diam, 400 - diam) != y:
			dy *= -1          
		balls[i] = x,y,dx,dy,r,c
		ellipse(x,y,r,r) 
    fill(0)      	
    text("fps: %3d"%frameRate,0,20)
        		

