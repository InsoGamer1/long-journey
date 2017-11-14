# Correct the song name based on title or (album-artist) if any

from eyed3 import id3
import os,sys


def getTag(songpath):
	global tag
	try:
		tag.parse(songpath)
		title = tag.title
		if title:
			return title
		elif tag.artist or tag.album:
			return " - ".join( str(x) for x in [tag.artist , tag.album] if x )
		else:
			return None
	except:
		return ""
	

def renameSongs(dir , logfile , filter=""):
	print dir
	log_dict = {}
	log_dict["DIR"]= dir
	log_dict["FILTER"]= filter
	log_dict["renamed"] = {}
	log_dict["error"]=[]
	log_dict["skipped"]=[]
	log_dict["parseError"]=[]
	os.chdir( dir)
	for i in os.listdir(dir):
		if filter in i and i.endswith(".mp3"):
			title = getTag(os.path.join( dir , i ))
			if title == "":
				print "*********\tParsedError- "+ i
				log_dict["parseError"].append(i)
			if not title:
				print "*********\tSkipped- "+ i
				log_dict["skipped"].append(i)
			else:
				title = str(title)
				if title != i :
					try:
						title = title.replace("|" , "")
						title = title.replace(":" , "")
						if not title.endswith(".mp3"):
							title += ".mp3"
						os.rename(i , title)
						print 'rename done : '+i+' -> ' + title
						log_dict["renamed"][i] = title
			 
					except Exception as inst:
						#print inst
						print 'Error in renaming', i , " to " , title
						log_dict["error"].append(i)
				
	if logfile:
		log_dict["LOG"]= os.path.join(dir , logfile )
		fd = open ( logfile , "w" )
		backupfd = sys.stdout
		sys.stdout = fd 
		print log_dict
		sys.stdout = backupfd 
		fd.close()
	return log_dict
	
	
def in_array(xx , array):
	for i in array:
		if xx in i:
			found = i.split(xx)[1]
			if found=="":
				return False
			else:
				return found
	return False	
	
def helper():
	print "Usage:"
	print "python " + __file__  + " options=<VALUES>"
	print "Options: "
	print "\t-d : Directory/Folder path | Default:Current Directory | optional"
	print "\t-f : filter-in | Default:Nothing | optional"
	print "\t-l : logfile | Default:Nothing | optional"
	print "Example : python correctSongs.py -d=C:\Users\Raheman\Desktop\AUD-Songs -l=AUD_songs_log5.txt -f=AUD"
	
if __name__=="__main__":
	dir =  in_array("-d=",sys.argv)
	logfile = in_array("-l=",sys.argv)
	filter =  in_array("-f=",sys.argv)
	# if len(sys.argv) > 1 and False in [dir,logfile,filter]:
	if len(sys.argv) > 1 and not ( dir or logfile or filter):
		helper()
		sys.exit()
	
	# Default options
	if not dir:
		dir = os.getcwd()
	if not logfile:
		logfile = None
	if not filter:
		filter = ""
	print "Options given :"
	print "\tDirectory " , dir
	print "\tLogfile " , logfile
	print "\tFilter " , filter
	print ""
		
	tag = id3.Tag()
	logDict= renameSongs( dir , logfile , filter)
	print "Renamed songs :"
	print "\n".join( "\t"+str(logDict["renamed"][i]) for i in logDict["renamed"] )
	print "Skipped songs :"
	print "\n".join( "\t"+str(i) for i in logDict["skipped"] )
	print "Error in renaming for songs :"
	print "\n".join( "\t"+str(i) for i in logDict["error"] )
	print "parseError for songs :"
	print "\n".join( "\t"+str(i) for i in logDict["parseError"] )
	