#/usr/bin/python
#SingletonPattern

class Singleton:
	'''
	A class with only ONE instace
	'''
	__instance = None
	@staticmethod
	def getInstance():
		return Singleton.__instance

	def __init__(self):
		if Singleton.__instance:
			raise Exception("Error : This class is a singleton!")
		else:
			Singleton.__instance = self

	def __repr__(self):
		return "This is singleton class :)"
			
s = Singleton()
print s , id(s)

try:
	p = Singleton()
except Exception as e:
	print e
	
q = Singleton.getInstance()
print q , id(q)
r = Singleton.getInstance()
print r , id(r)
t = Singleton.getInstance()
print t , id(t)