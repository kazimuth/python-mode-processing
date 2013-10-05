#run before every sketch; define things

import info.sansgills.mode.python.wrapper.PythonPApplet as PythonPApplet
import processing.core.PApplet as PApplet
import processing.opengl.PShader as PShader
import processing.core.PFont as PFont
import processing.core.PVector as RealPVector
import processing.core.PConstants as PConstants

#from processing.py
class PVector(object): 
    @classmethod
    def __new__(cls, *args):
        return RealPVector(*args[1:])
    @classmethod
    def add(cls, a, b, dest=None):
        return RealPVector.add(a, b, dest)
    @classmethod
    def sub(cls, a, b, dest=None):
        return RealPVector.sub(a, b, dest)
    @classmethod
    def mult(cls, a, b, dest=None):
        return RealPVector.mult(a, b, dest)
    @classmethod
    def div(cls, a, b, dest=None):
        return RealPVector.div(a, b, dest)
    @classmethod
    def cross(cls, a, b, dest=None):
        return RealPVector.cross(a, b, dest)
    @classmethod
    def dist(cls, a, b):
        return RealPVector.dist(a, b)
    @classmethod
    def dot(cls, a, b):
        return RealPVector.dot(a, b)
    @classmethod
    def angleBetween(cls, a, b):
        return RealPVector.angleBetween(a, b)

def monkeypatch_method(cls):
    def decorator(func):
        setattr(cls, func.__name__, func)
        return func
    return decorator

@monkeypatch_method(RealPVector)
def __sub__(a, b):
    return PVector(a.x - b.x, a.y - b.y, a.z - b.z)

@monkeypatch_method(RealPVector)
def __add__(a, b):
    return PVector(a.x + b.x, a.y + b.y, a.z + b.z)

@monkeypatch_method(RealPVector)
def __mul__(a, b):
    if isinstance(b, RealPVector): 
        raise TypeError("The * operator can only be used to multiply a PVector by a scalar")
    return PVector(a.x * b, a.y * b, a.z * b)

__applet__ = PythonPApplet()
this = __applet__
g = globals()
	
for constant in PythonPApplet.constants:
	g[constant] = PConstants.__dict__[constant]._doget(__applet__)

getframeRate = __applet__.getframeRate
getkeyPressed = __applet__.getkeyPressed
getmousePressed = __applet__.getmousePressed

alpha = __applet__.alpha
ambient = __applet__.ambient
ambientLight = __applet__.ambientLight
applyMatrix = __applet__.applyMatrix
arc = __applet__.arc
background = __applet__.background
beginCamera = __applet__.beginCamera
beginContour = __applet__.beginContour
beginRaw = __applet__.beginRaw
beginRecord = __applet__.beginRecord
beginShape = __applet__.beginShape
bezier = __applet__.bezier
bezierDetail = __applet__.bezierDetail
bezierPoint = __applet__.bezierPoint
bezierTangent = __applet__.bezierTangent
bezierVertex = __applet__.bezierVertex
blend = __applet__.blend
blendMode = __applet__.blendMode
blue = __applet__.blue
box = __applet__.box
brightness = __applet__.brightness
camera = __applet__.camera
clear = __applet__.clear
color = __applet__.color
colorMode = __applet__.colorMode
copy = __applet__.copy
createFont = __applet__.createFont
createGraphics = __applet__.createGraphics
createImage = __applet__.createImage
createInput = __applet__.createInput
createOutput = __applet__.createOutput
createReader = __applet__.createReader
createShape = __applet__.createShape
createWriter = __applet__.createWriter
cursor = __applet__.cursor
curve = __applet__.curve
curveDetail = __applet__.curveDetail
curvePoint = __applet__.curvePoint
curveTangent = __applet__.curveTangent
curveTightness = __applet__.curveTightness
curveVertex = __applet__.curveVertex
directionalLight = __applet__.directionalLight
ellipse = __applet__.ellipse
ellipseMode = __applet__.ellipseMode
emissive = __applet__.emissive
endCamera = __applet__.endCamera
endContour = __applet__.endContour
endRaw = __applet__.endRaw
endRecord = __applet__.endRecord
endShape = __applet__.endShape
exit = __applet__.exit
fill = __applet__.fill
filter = __applet__.filter
frameRate = __applet__.frameRate
frustum = __applet__.frustum
get = __applet__.get
hint = __applet__.hint
hue = __applet__.hue
image = __applet__.image
imageMode = __applet__.imageMode
lerpColor = __applet__.lerpColor
lightFalloff = __applet__.lightFalloff
lightSpecular = __applet__.lightSpecular
lights = __applet__.lights
line = __applet__.line
loadBytes = __applet__.loadBytes
loadFont = __applet__.loadFont
loadImage = __applet__.loadImage
loadJSONArray = __applet__.loadJSONArray
loadJSONObject = __applet__.loadJSONObject
loadPixels = __applet__.loadPixels
loadShader = __applet__.loadShader
loadShape = __applet__.loadShape
loadStrings = __applet__.loadStrings
loadTable = __applet__.loadTable
loadXML = __applet__.loadXML
loop = __applet__.loop
millis = __applet__.millis
modelX = __applet__.modelX
modelY = __applet__.modelY
modelZ = __applet__.modelZ
noCursor = __applet__.noCursor
noFill = __applet__.noFill
noLights = __applet__.noLights
noLoop = __applet__.noLoop
noSmooth = __applet__.noSmooth
noStroke = __applet__.noStroke
noTint = __applet__.noTint
noise = __applet__.noise
noiseDetail = __applet__.noiseDetail
noiseSeed = __applet__.noiseSeed
normal = __applet__.normal
ortho = __applet__.ortho
parseXML = __applet__.parseXML
perspective = __applet__.perspective
point = __applet__.point
pointLight = __applet__.pointLight
popMatrix = __applet__.popMatrix
popStyle = __applet__.popStyle
printCamera = __applet__.printCamera
printMatrix = __applet__.printMatrix
printProjection = __applet__.printProjection
pushMatrix = __applet__.pushMatrix
pushStyle = __applet__.pushStyle
quad = __applet__.quad
quadraticVertex = __applet__.quadraticVertex
random = __applet__.random
randomGaussian = __applet__.randomGaussian
randomSeed = __applet__.randomSeed
rect = __applet__.rect
rectMode = __applet__.rectMode
red = __applet__.red
redraw = __applet__.redraw
requestImage = __applet__.requestImage
resetMatrix = __applet__.resetMatrix
resetShader = __applet__.resetShader
rotate = __applet__.rotate
rotateX = __applet__.rotateX
rotateY = __applet__.rotateY
saturation = __applet__.saturation
save = __applet__.save
saveBytes = __applet__.saveBytes
saveFrame = __applet__.saveFrame
saveJSONArray = __applet__.saveJSONArray
saveJSONObject = __applet__.saveJSONObject
saveStream = __applet__.saveStream
saveStrings = __applet__.saveStrings
saveTable = __applet__.saveTable
saveXML = __applet__.saveXML
scale = __applet__.scale
screenX = __applet__.screenX
screenY = __applet__.screenY
screenZ = __applet__.screenZ
selectFolder = __applet__.selectFolder
selectInput = __applet__.selectInput
selectOutput = __applet__.selectOutput
shader = __applet__.shader
shape = __applet__.shape
shapeMode = __applet__.shapeMode
shearX = __applet__.shearX
shearY = __applet__.shearY
shininess = __applet__.shininess
size = __applet__.size
smooth = __applet__.smooth
specular = __applet__.specular
sphere = __applet__.sphere
sphereDetail = __applet__.sphereDetail
spotLight = __applet__.spotLight
stroke = __applet__.stroke
strokeCap = __applet__.strokeCap
strokeJoin = __applet__.strokeJoin
strokeWeight = __applet__.strokeWeight
text = __applet__.text
textAlign = __applet__.textAlign
textAscent = __applet__.textAscent
textDescent = __applet__.textDescent
textFont = __applet__.textFont
textLeading = __applet__.textLeading
textMode = __applet__.textMode
textSize = __applet__.textSize
textWidth = __applet__.textWidth
texture = __applet__.texture
textureMode = __applet__.textureMode
tint = __applet__.tint
translate = __applet__.translate
triangle = __applet__.triangle
updatePixels = __applet__.updatePixels
vertex = __applet__.vertex

abs = PApplet.abs
acos = PApplet.acos
append = PApplet.append
arrayCopy = PApplet.arrayCopy
asin = PApplet.asin
atan = PApplet.atan
atan2 = PApplet.atan2
binary = PApplet.binary
blendColor = PApplet.blendColor
ceil = PApplet.ceil
concat = PApplet.concat
constrain = PApplet.constrain
cos = PApplet.cos
createInput = PApplet.createInput
createOutput = PApplet.createOutput
createReader = PApplet.createReader
createWriter = PApplet.createWriter
day = PApplet.day
debug = PApplet.debug
degrees = PApplet.degrees
dist = PApplet.dist
#exec = PApplet.exec
exp = PApplet.exp
expand = PApplet.expand
floor = PApplet.floor
hex = PApplet.hex
hour = PApplet.hour
join = PApplet.join
lerp = PApplet.lerp
lerpColor = PApplet.lerpColor
loadBytes = PApplet.loadBytes
loadStrings = PApplet.loadStrings
log = PApplet.log
mag = PApplet.mag
map = PApplet.map
match = PApplet.match
matchAll = PApplet.matchAll
max = PApplet.max
min = PApplet.min
minute = PApplet.minute
month = PApplet.month
nf = PApplet.nf
nfc = PApplet.nfc
nfp = PApplet.nfp
nfs = PApplet.nfs
norm = PApplet.norm
pow = PApplet.pow
#print = PApplet.print
println = PApplet.println
radians = PApplet.radians
reverse = PApplet.reverse
round = PApplet.round
saveBytes = PApplet.saveBytes
saveStream = PApplet.saveStream
saveStrings = PApplet.saveStrings
second = PApplet.second
shorten = PApplet.shorten
sin = PApplet.sin
sort = PApplet.sort
splice = PApplet.splice
split = PApplet.split
splitTokens = PApplet.splitTokens
sq = PApplet.sq
sqrt = PApplet.sqrt
subset = PApplet.subset
tan = PApplet.tan
trim = PApplet.trim
unbinary = PApplet.unbinary
unhex = PApplet.unhex
year = PApplet.year

def setPixel(*args):
    __applet__.set(*args)

del monkeypatch_method, g, __sub__, __add__, __mul__, PApplet, PConstants, PythonPApplet