import os,sys
from stat import *
import re
import eyed3
eyed3.log.setLevel("ERROR")




'''
#rename to correct format , if dup , delete the songs
for i in os.listdir ( os.getcwd()):
	if ( not 'AlbumA' in i ) and ( i.startswith('[Songs') or i.startswith('[songs')):
		grp = re.search ( r'\[.*\][ \d-]+(.*)' , i)
		if grp:
			dest =  grp.group(1)
			try:
				os.rename ( i , dest )
				print i+" >> " + dest 
			except:
				os.remove( i )
				print "Removed "+ i
'''	

'''
# removes duplicate songs based on its length
listf = os.listdir ( os.getcwd())
dup_list = []
dict = {}
for i in listf:
	if 'AlbumA' not in i:
		try:
			dict [ os.stat(i).st_size ] +=  ","+i
			dup_list.append ( os.stat(i).st_size )
		except:
			dict [ os.stat(i).st_size ] = i 

for i in dup_list:
	dup_songs = dict[i].split(",")
	for x in range( len( dup_songs)):
		try:
			f1 = dup_songs[x]
			f2 = dup_songs[x+1]
			print f1 , '\t', f2
			if eyed3.load(f1).info.time_secs == eyed3.load(f2).info.time_secs:
				if ' - Copy' in f1 or re.search ( r'[ \d-].*' , f1):
					bakra = f1
				else:
					bakra = f2
				os.remove( bakra)
				print '\t Deleted : '+bakra 
		except:
			pass
'''		
			







	


