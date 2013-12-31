size(100, 100, P3D)
noFill()
fov = PI/3.0
cameraZ = (height/2.0) / tan(fov/2.0)
perspective(fov, float(width)/float(height), 
            cameraZ/10.0, cameraZ*10.0)
translate(50, 50, 0)
rotateX(-PI/6)
rotateY(PI/3)
smooth(4)
strokeWeight(2)
box(45)

