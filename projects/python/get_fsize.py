#top #

import os
import sys
from collections import defaultdict 
import time

#globals
size_cache_g = {}
## defaults
cwd = os.getcwd()
rec_level_g = 3
exceptionHit = 0

if len(sys.argv) > 1:
	cwd = sys.argv[1]
if len(sys.argv) > 2:
	rec_level_g = int(sys.argv[2])

def cachesize_dec(some_function):
	def wrapper(*args, **kwargs):
		global size_cache_g
		if args[0] in size_cache_g:
			res = size_cache_g[args[0]]
		else:
			res = some_function(*args, **kwargs)
			size_cache_g[args[0]] = res
		return res
	return wrapper

def convert_bytes(num):
    """
    this function will convert bytes to MB.... GB... etc
    """
    for x in ['bytes', 'KB', 'MB', 'GB', 'TB']:
        if num < 1024.0:
            return "%3.1f %s" % (num, x)
        num /= 1024.0

@cachesize_dec 
def file_size(file_path):
	"""
	this function will return the file size
	"""
	if os.path.isdir(file_path):
		sizecache = 0
		for i in os.listdir( file_path ):
			sizecache += file_size( os.path.join( file_path , i ))
		return sizecache
	else:
		try:
			file_info = os.stat(file_path)
			return file_info.st_size
		except:
			global exceptionHit
			exceptionHit += 1
			return 0
		
def getTopfile(dir,rec_level,parent):
	tree_rep = "|  "
	print "%s%s ===> %s "%(tree_rep*(rec_level_g-rec_level),parent , convert_bytes(file_size( dir )))
	if rec_level > 0:
		rec_level -= 1
		if os.path.isdir(dir):
			for i in os.listdir( dir ):
				fullpath = os.path.join (  dir , i )
				getTopfile ( fullpath ,rec_level , i )
		

def main():
	start_time = time.time()
	getTopfile ( cwd , rec_level_g , cwd )
	if exceptionHit>0:
		print "Warning : %d exception hits !!!!"%exceptionHit
	print("--- %s seconds ---" % (time.time() - start_time))

main()
