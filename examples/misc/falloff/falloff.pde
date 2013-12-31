
def setup():
    size(200, 200, P3D)
    noStroke()

def draw():
    background(200)
    if mousePressed: lightFalloff(1,0,1e-4)
    pointLight(255,255,255,100,100,50)
    nx = 6
    ny = 6
    for i in range(nx):
        for j in range(ny):
            pushMatrix()
            translate((i+0.5)*width/nx,(j+0.5)*height/ny)
            sphere(10)
            popMatrix()


