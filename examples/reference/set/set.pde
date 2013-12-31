def setup():
	size(400, 400)
	for i in range(30,width-15):
		for j in range(20,height-25):
			blue = noise(i, j)*255
			c = color(0, 0, blue)
			set(i, j, c)


