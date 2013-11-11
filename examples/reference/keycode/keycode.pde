################
# As of Nov 20 2012 UP and DOWN keyCode not supported
# hence fudge to use "u" and "d"
###############

def setup():
  size(100, 100)
  global fillVal
  fillVal = color(126)

def draw():
  global fillVal	
  fill(fillVal)
  rect(25, 25, 50, 50)

def keyPressed():
  global fillVal
  if (key != CODED):
    if (key == "u"):
      fillVal = 255
    elif (key == "d"):
      fillVal = 0
  else:
    fillVal = 126

