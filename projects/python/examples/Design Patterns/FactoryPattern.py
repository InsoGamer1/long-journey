#/usr/bin/python
#FactoryPattern

class AuthFactory:
	'''
	Creates a family of object types/classes
	ex: 
		- family of Person type , that can Male, Female
		- family of AuthMechanism , can be Basic,Token or Certificate
		basicAuth = AuthFactory.getAuthMechanism("Basic")
	'''
	@staticmethod
	def getAuthMechanism(type):
		if type=="Basic":
			return AuthFactory.Basic()
		elif type=="Token":
			return AuthFactory.Token()
		elif type=="Cert":
			return AuthFactory.Certificate()
		else:
			raise Exception("Invalid AuthMechanism")
			
	class Basic:
		def __init__(self):
			pass
		def __repr__(self):
			return self.__class__.__name__
			
	class Token:
		def __init__(self):
			pass
		def __repr__(self):
			return self.__class__.__name__
			
	class Certificate:
		def __init__(self):
			pass
		def __repr__(self):
			return self.__class__.__name__
		
basicAuth = AuthFactory.getAuthMechanism("Basic")
print basicAuth , basicAuth.__class__.__name__
tokenAuth = AuthFactory.getAuthMechanism("Token")
print tokenAuth , tokenAuth.__class__.__name__
certAuth = AuthFactory.getAuthMechanism("Cert")
print certAuth , certAuth.__class__.__name__