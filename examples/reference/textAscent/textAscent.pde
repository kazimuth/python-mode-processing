
font = createFont("Helvetica", 32) 
textFont(font)

textSize(32) 
ascent = textAscent()
text("dp", 0, 70)
line(0, 70-ascent, 100, 70-ascent) 

textSize(64)
ascent = textAscent()
text("dp", 35, 70)
line(35, 70-ascent, 100, 70-ascent)

