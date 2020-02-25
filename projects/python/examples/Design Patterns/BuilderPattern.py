#/usr/bin/python
#BuilderPattern


class Coffee:
	'''
	Builder Pattern : 
		* Seperates object creation from its representation
		* Class should provide multiple ways of object creation 
	example : 
		A Coffee class that provides muliple way of creating coffee
		coffee = new Coffee.Builder("mocha").sugar(True).milk(True).size("Large").build()
	'''
	def __init__(self,builder):
		self._type = builder._type 
		self._sugar = builder._sugar
		self._milk = builder._milk 
		self._size = builder._size
		
	class Builder:
		def __init__(self,type,sugar=True,milk=True,size="Small"):
			self._type = type
			self._sugar = sugar
			self._milk = milk
			self._size = size
			
		def sugar(self,option):
			self._sugar = option
			return self
			
		def milk(self,option):
			self._milk = option
			return self
			
		def size(self,option):
			self._size = option
			return self
			
		def build(self):
			# print "Coffee ( type:%s sugar:%s milk:%s size:%s )"%(self._type,
				# self._sugar,self._milk,self._size)
			return Coffee(self)

	def __repr__(self):
		return "Coffee ( type:%s sugar:%s milk:%s size:%s )"%(self._type,
			self._sugar,self._milk,self._size)
				
coffee = Coffee.Builder("Mocha").sugar(False).milk(True).size("Medium").build()
print coffee
coffee1 = Coffee.Builder("Mocha").sugar(True).milk(False).size("Large").build()
print coffee1

				
