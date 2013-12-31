

def setup():
  size(100, 100)
  noFill()
  noLoop()

def draw():
  curveDetail(1)
  drawCurves(-15)
  stroke(126)
  curveDetail(2)
  drawCurves(0)
  stroke(255)
  curveDetail(4)
  drawCurves(15)

def drawCurves(y):
  curve( 5, 28+y,  5, 28+y, 73, 26+y, 73, 63+y)
  curve( 5, 28+y, 73, 26+y, 73, 63+y, 15, 67+y) 
  curve(73, 26+y, 73, 63+y, 15, 67+y, 15, 67+y)


