import os,sys
from stat import *
import re

list = []
print 'in here'

'''
#extra = '[Songs.PK] '
extra = '[SongsPK.info] '

for i in os.listdir(os.getcwd()):
	if extra  in i:
		print 'renaming ' + i
		try:
			os.rename(i, i[len(extra):])
			print 'rename done : '+i+' -> ' + i[len(extra):]
 
		except:
			if os.stat(i).st_size <= os.stat(i[len(extra ):]).st_size:
				print 'deleting ' + i
				list.append(i)
				os.remove(i)
			else:
				print 'deleting ' + i[len(extra):]
				list.append(i[len(extra):])
				os.remove(i[len(extra):])
print 'Printing the deleted files'
for x in list :
	print x
'''

'''
for i in os.listdir(os.getcwd()):
	mat = re.match('^[ -_]+', i)
	if mat:
		extra = mat.group()
		print 'renaming to ' + i[len(extra):]
		
		try:
			os.rename(i, i[len(extra):])
			print 'rename done : '+i+' -> ' + i[len(extra):]
 
		except:
			if os.stat(i).st_size <= os.stat(i[len(extra ):]).st_size:
				print 'deleting ' + i
				list.append(i)
				os.remove(i)
			else:
				print 'deleting ' + i[len(extra):]
				list.append(i[len(extra):])
				os.remove(i[len(extra):])
print 'Printing the deleted files'
for x in list :
	print x
sys.exit()
'''
fd = open ( 'log.txt' , "r" )
for line in fd :
	if 'rename done : ' in line:
		str = line[ len('rename done : '):].strip('\n\r')
		dest , src = str.split(' -> ')
		src = src.lstrip(' ')
		print src + '->' + dest

		
		try:
			os.rename(src , dest)
			print 'rename done : '+src+' -> ' + dest
 
		except:
			print 'error in ' + src
		
fd.close()





















	


