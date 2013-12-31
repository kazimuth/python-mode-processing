size(100, 100, P3D)
noFill()
translate(50, 50, 0)
rotateY(PI/6) 
stroke(153)
box(35)
# Set rotation angles
ct = cos(PI/9.0)
st =sin(PI/9.0)          
#Matrix for rotation around the Y axis
applyMatrix(  ct, 0.0,  st,  0.0,
             0.0, 1.0, 0.0,  0.0,
             -st, 0.0,  ct,  0.0,
             0.0, 0.0, 0.0,  1.0)  
stroke(255)
box(50)


