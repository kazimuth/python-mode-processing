#commented prepend.py, for the curious
#WHO'S UP FOR SOME BLACK MAGIC

#our imports!
import info.sansgills.mode.python.wrapper.PythonPApplet as PythonPApplet
import processing.core.PApplet as PApplet
import processing.core.PShader as PShader
import processing.core.PFont as PFont
import processing.core.PVector as RealPVector

#messing with PVectors
#thanks to processing.py!

class PVector(object): #making it so that the PVector static methods don't complain when called with two arguments
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

#a handy tool to override methods at runtime
def monkeypatch_method(cls):
    def decorator(func):
        setattr(cls, func.__name__, func)
        return func
    return decorator
	
#Patching PVector with actual operations!
@monkeypatch_method(RealPVector)
def __sub__(a, b):
    return PVector(a.x - b.x, a.y - b.y, a.z - b.z)
	
@monkeypatch_method(RealPVector)
def __add__(a, b):
    return PVector(a.x + b.x, a.y + b.y, a.z + b.z)
	
@monkeypatch_method(RealPVector)
def __mul__(a, b):
    if isinstance(b, RealPVector): #woah there
        raise TypeError("The * operator can only be used to multiply a PVector by a scalar")
    return PVector(a.x * b, a.y * b, a.z * b)


#now onto PApplet stuff

__applet__ = PythonPApplet()

#note: python stores all of its global variables in a dictionary, which can be accessed with globals()
#I am going to abuse this fact

#populate constants 
#TODO (they're mostly PI, figure out where to get it)

#populate static methods (which I've enumerated in PythonPApplet.java)
for method in PythonPApplet.staticMethods:
	#make the global function $method point to PApplet.$method
	globals()[method] = PApplet.__dict__[method]

#populate instance methods
for method in PythonPApplet.instanceMethods:
	#this is kinda weird
	#make a lambda that returns __applet__.$method(*args)
	#but we're geting $method static from PApplet we have to bind it to an instance
	#so we have to call PApplet.$method(instance, *args)
	globals()[method] = lambda *args: PApplet.__dict__[method](__applet__, *args)
	#note: i would use $method.__get__ but java-derived functions don't have it in jython :(



#get rid of non-processing names
#(don't worry, the important things are still hanging around in memory)
del monkeypatch_method, RealPVector, PApplet, PythonPApplet