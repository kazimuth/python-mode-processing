

def setup():
  size(400, 400)  
  noFill()
  noLoop()
  
def draw():
  background(100)
  stroke(255, 70)
  strokeWeight(8)
  bezierDetail(12)
  bezier(340, 80, 40, 40, 360, 360, 60, 380)
  bezierDetail(1)
  strokeWeight(1)
  stroke(0, 70)
  bezier(340, 80, 40, 40, 360, 360, 60, 380)

