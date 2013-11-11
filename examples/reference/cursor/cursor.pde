
# Move the mouse left and right across the image
# to see the cursor change from a cross to a hand

def draw():
  if mouseX < 50:
    cursor(CROSS)
  else:
    cursor(HAND)

