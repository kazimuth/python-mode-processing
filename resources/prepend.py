#see PythonMode/prepend-commented.py for something sensible

import info.sansgills.mode.python.wrapper.PythonPApplet as PythonPApplet
import processing.core.PApplet as PApplet
import processing.opengl.PShader as PShader
import processing.core.PFont as PFont
import processing.core.PVector as RealPVector

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
g = globals()

getmousePressed = __applet__.getmousePressed
getkeyPressed = __applet__.getkeyPressed

for method in PythonPApplet.staticMethods:
	g[method] = PApplet.__dict__[method]

def get_inst(method):
	return lambda *args: PApplet.__dict__[method](__applet__, *args)

for method in PythonPApplet.instanceMethods:
	g[method] = get_inst(method)

del monkeypatch_method, g, __sub__, __add__, __mul__, method, get_inst