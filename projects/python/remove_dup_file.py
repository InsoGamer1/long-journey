#__AUTHOR__ : MUJTABA RAHEMAN
#__ACTION__ : remove duplicate files from directory based on it's size and extension
import os,sys,time
import argparse

class RemoveDups(object):
	def __init__( self , dir ,dryrun=False,force=False):
		self.f_dict = {}
		self.dir = dir
		self.size_dict = {}
		self.duplicate_files = []
		self.deleted_files = []
		self.dryrun = dryrun
		self.force = force
		self.logname = "duplicate.log"
		self.logfile = os.path.join( dir , self.logname )
		self.logbuff =  "-------------------" + time.asctime() +"-------------------\n"
		
		try:
			if not dryrun:
				self.logfilefd = open( self.logfile , "a+" )
		except:
			pass
	def write_log ( self , buff):
		print "%s : %s"%(__file__,buff)
		self.logbuff += str( buff ) + "\n"
		
	@staticmethod
	def check2( files ):
		res =[]
		for ff in files:
			with open( ff , "r" ) as fd:
				res.append(fd.read(1024))
		return all( res[i]==res[0] for i in range(1,len(res)) )
		
	@staticmethod
	def check1( files ):
		res = map( lambda x:x.split(".")[-1] , files )
		return all( res[i]==res[0] for i in range(1,len(res)) )
	@staticmethod
	def check0 ( files):
		return True
		
	@staticmethod
	def scandir ( folder , fileList=[]):
		for file in os.listdir( folder ):
			fullName = os.path.join( folder , file )
			if os.path.isdir(fullName):
				fileList = RemoveDups.scandir( fullName , fileList)
			else:
				fileList.append( fullName )
		return fileList
				
	def scan( self , filters=[] ):
		count = 0
		print "Scanning in %s with filters %s ( if any )" %( self.dir , filters) 
		for fileName in os.listdir( self.dir ):
			recFilesList = [fileName]
			if os.path.isdir(os.path.join( self.dir , fileName )):#file
				recFilesList = RemoveDups.scandir( os.path.join( self.dir , fileName ) )
			for recFile in recFilesList:
				if any(map(lambda filter:recFile.endswith(filter),filters )):#match with any of filter
					# f_dict = { recFile : fileSize }
					count+=1
					self.f_dict[recFile]= os.stat(os.path.join(self.dir , recFile )).st_size 
			
				
		
		for fname in self.f_dict:
			# size_dict = { fileSize : [ fileName1 , fileName2 ] }
			try:
				self.size_dict[ self.f_dict[fname] ].append ( fname )
			except:
				self.size_dict[ self.f_dict[fname] ] = [ fname ]
		print "=====================",count
	
	def get_duplicate(self):
		#duplicate_files = [ [ duplicateFileName1,duplicateFileName2,fileSize] , ... ]
		for fileSize in self.size_dict:
			if len(self.size_dict[fileSize])>1:
				duplicateFileNames = self.size_dict[fileSize]
				self.duplicate_files.append(duplicateFileNames+[fileSize])
		return self.duplicate_files
		
	def printDuplicates(self):
		for x in self.duplicate_files:# [ [ duplicateFileName1,duplicateFileName2,fileSize] , ... ]
			print "["
			print "\t--- size : %d"%(x[-1])
			print "\n".join ( ["\t"+str(f) for f in x[:-1] ])
			print "]\n"

	def delete( self , files ):
		files = map( lambda x:os.path.join(self.dir,x) , files )
		files = sorted( files , key=len , reverse=True)#longer first
		if RemoveDups.check0(files) and RemoveDups.check1(files) and RemoveDups.check2(files):
			for ff in files[:-1]:#shortest filename will not be deleted
				try:
					if not self.dryrun:
						if self.force:
							os.unlink( ff )
							self.write_log( "--> Deleted %s..."%(ff) )
							self.deleted_files.append ( ff )
						else:
							print "Delete %s from \n\t%s"%(ff,"\n\t".join( files ) )
							if raw_input("y/n ----> ")=="y": 
								os.unlink( ff )
								self.write_log( "--> Deleted %s..."%(ff) )
								self.deleted_files.append ( ff )
							else: print "skipped %s"%ff
					
				except Exception,e:
					# print "Delete error " , e
					self.write_log ( "   XXXX Delete error :  %s"%(ff) )
							
	def delete_all( self ):
		for files in self.duplicate_files:
			filesWithoutSize = files[:-1]
			self.delete( filesWithoutSize )
					
	def show_deleted( self ):
		print "\nDeleted files are : "
		print "\n".join ( self.deleted_files )
		
	def __del__( self ):
		if not self.dryrun:
			self.logfilefd.write( self.logbuff )
			self.logfilefd.close()
			print "log file @ " , self.logfile
		
def main():
	parser = argparse.ArgumentParser()
	parser.add_argument("-d", "--dir", help="directory absolute path to be scanned",dest="dir",
                    default=os.getcwd())
	parser.add_argument("--dryrun", help="shows to be deleted files without deleting",
                    action="store_true")
	parser.add_argument("-ff","--filter", help="filter of fileType ex: .mp4,.mkv",default="",dest="filter",)
	parser.add_argument("-f","--force", help="delete without promt",default=False,action="store_true")
	args = parser.parse_args()
	print "---------------------------------------------------"
	print "Directory : \t"+str(args.dir)
	print "Filter(s) : \t"+str(args.filter)
	print "DryRun : \t"+str(args.dryrun)
	print "Force : \t"+str(args.force)
	print "---------------------------------------------------\n"
		
	cwd = args.dir
	if not os.path.exists(cwd):
		print "Error in path " ,  cwd
		exit()
	# print "\n".join( RemoveDups.scandir(r"F:\root\Movies\Downloads\Triangle (2009)") )
	# exit()
	rd = RemoveDups( cwd ,args.dryrun , args.force)
	rd.scan(args.filter.rstrip().split(","))
	rd.get_duplicate()
	
	print "Duplicates are : "
	rd.printDuplicates()
	rd.delete_all()
	rd.show_deleted()
	if args.dryrun:
		print "\n-------------- DryRun: No Files are deleted!!!! ---------------"
	
if __name__=="__main__":
	main()
	
	
