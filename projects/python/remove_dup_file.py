#__AUTHOR__ : MUJTABA RAHEMAN
#__ACTION__ : remove duplicate files from directory based on it's size and extension
import os,sys,time

class RemoveDups(object):
	def __init__( self , dir ):
		self.f_dict = {}
		self.dir = dir
		self.size_dict = {}
		self.duplicate_files = []
		self.deleted_files = []
		logname = "duplicate.log"
		self.logfile = os.path.join( dir , logname )
		self.logbuff =  "-------------------" + time.asctime() +"-------------------\n"
		try:
			self.logfilefd = open( self.logfile , "a+" )
		except:
			pass
	def write_log ( self , buff):
		print "LOG : " , buff
		self.logbuff += str( buff ) + "\n"
		
	@staticmethod
	def check2( f1 , f2 ):
		c1 = ""
		c2 = ""
		with open( f1 , "r" ) as fd:
			c1 = fd.read()[:50]
		with open( f2 , "r" ) as fd:
			c2 = fd.read()[:50]
		return c1==c2
	@staticmethod
	def check1( f1 , f2 ):
		return f1.split(".")[-1] == f2.split(".")[-1]
	@staticmethod
	def check0 ( f1,f2):
		return True
		
	def scan( self , filter="" ):
		print "Scanning in %s with filter %s ( if any )" %( self.dir , filter) 
		self.f_dict = { i:os.stat(os.path.join(self.dir , i )).st_size  for i in os.listdir( self.dir ) if filter in i }
		for fname in self.f_dict:
			try:
				self.size_dict[ self.f_dict[fname] ].append ( fname )
			except:
				self.size_dict[ self.f_dict[fname] ] = [ fname ]
	
	def get_duplicate(self):
		self.duplicate_files = [ self.size_dict[x] for x in self.size_dict if len(self.size_dict[x])>1 ] 
		return self.duplicate_files

	def delete( self , f1name , f2name ):
		f1  = os.path.join(self.dir , f1name )
		f2 = os.path.join(self.dir , f2name )
		self.write_log ( "checking :"+ f1name +" with " + f2name )
		if RemoveDups.check0(f1,f2) and RemoveDups.check1(f1,f2) and RemoveDups.check2(f1,f2):
			try:
				self.write_log( "--> Deleting %s..."%(f1name) )
				os.unlink( f1 )
				self.deleted_files.append ( f1 )
			except Exception,e:
				# print "Delete error " , e
				self.write_log ( "   XXXX Delete error :  %s"%(f1name) )
							
	def delete_all( self ):
		for files in self.duplicate_files:
			for i in range( len(files)):
				for j in range(i+1 , len(files) ):
					self.delete( files[i] , files[j] )
					
	def show_deleted( self ):
		print "Deleted files are : "
		print "\n".join ( self.deleted_files )
		
	def __del__( self ):
		self.logfilefd.write( self.logbuff )
		self.logfilefd.close()
		print "log file @ " , self.logfile
		
def main():
	cwd = os.getcwd()
	if len( sys.argv ) == 2:
		cwd = sys.argv[1]
	if not os.path.exists(cwd):
		print "Error in path " ,  cwd
		exit()
	rd = RemoveDups( cwd )
	rd.scan(".mp4")
	dups = rd.get_duplicate()
	print "duplicates are : "
	print "\n".join ( [str(x) for x in dups] )
	rd.delete_all()
	rd.show_deleted()
	
if __name__=="__main__":
	main()
	