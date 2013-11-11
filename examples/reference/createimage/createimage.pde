def setup():
    global a
    size(200,200)
    a = createImage(120, 120, ARGB)
    for i in range(a.width*a.height):
        a.pixels[i] = color(0,90,102, i%a.width * 2)
    a.updatePixels()

    
def draw():
    global a
    background(204)
    image(a, 33, 33)
    image(a, mouseX - 60, mouseY - 60)

