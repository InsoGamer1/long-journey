#/usr/bin/python
#PrototypePattern

import copy
class PrototypePattern:
	'''
	Prototype Pattern : 
		* A fully initialised instance to be copied or clone
	example : 
		1. A chess game with initial setup
		2. A carrom game with initial setup
		3. A ZeroedList ( list with its item as zero )
		
	'''
	def __init__(self,builder):
		pass
		
	class Chess:
		def __init__(self):
			print "I am called only once"
			self.data = {
				"height": 8,
				"width": 8,
				"white":{
					"king": { "count": 1,"position": ["1e"] },
					"queen": { "count": 1,"position": ["1d"] },
					"rooks": { "count": 2,"position": ["1a","1h"] },
					"bishops": { "count": 2,"position": ["1c","1f"] },
					"knights": { "count": 2,"position": ["1b","1g"] },
					"pawns": { "count": 8,"position": ["2"+i for i in "abcdefgh"] }
				},
				"black":{
					"king": { "count": 1,"position": ["8e"] },
					"queen": { "count": 1,"position": ["8d"] },
					"rooks": { "count": 2,"position": ["8a","8h"] },
					"bishops": { "count": 2,"position": ["8c","8f"] },
					"knights": { "count": 2,"position": ["8b","8g"] },
					"pawns": { "count": 8,"position": ["7"+i for i in "abcdefgh"] }
				}
			}
	
		def __repr__(self):
			return str(self.data)
		
		def clone(self):
			return copy.deepcopy(self.data)
			
	class ZeroedList():
		def __init__(self,limit):
			self.list=[ 0 for _ in range(limit) ]
		def __repr__(self):
			return str(self.list)
				
chess = PrototypePattern.Chess()
print chess
chess1 = chess.clone()
print "chess1 ", id(chess1)
chess2 = chess.clone()
print "chess2 ", id(chess2)
chess3 = chess.clone()
print "chess3 ", id(chess3)

zl = PrototypePattern.ZeroedList(10)
print zl

				
