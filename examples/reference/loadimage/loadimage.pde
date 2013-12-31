

def setup():
	size(500, 500)
	img = loadImage("data/wig.png")
	img2 = loadImage("data/crackle.png") 
	blend(img2, 0, 0, 33, 100, 67, 0, 33, 100, ADD)
	
	image(img, 0, 0)
	loadPixels()
	image(img2, 0, 0)
	updatePixels()
	#print(dir(img))



