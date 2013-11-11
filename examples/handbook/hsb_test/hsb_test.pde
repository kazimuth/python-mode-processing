
# Change the saturation and brightness, hue constant
size(100, 100)
colorMode(HSB)
for i in range(100):
  for j in range(100):
    stroke(132, j*2.5, i*2.5)
    point(i, j)


