

noFill()
curve(5, 26, 5, 26, 73, 24, 73, 61)
curve(5, 26, 73, 24, 73, 61, 15, 65) 
ellipseMode(CENTER)
steps = 6
fill(255)
for i in range(steps+1):
  t = i / float(steps)
  x = curvePoint(5, 5, 73, 73, t)
  y = curvePoint(26, 26, 24, 61, t)
  ellipse(x, y, 5, 5)
  x = curvePoint(5, 73, 73, 15, t)
  y = curvePoint(26, 24, 61, 65, t)
  ellipse(x, y, 5, 5)

