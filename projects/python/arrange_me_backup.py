#Author : Mujtaba Raheman
import os,shutil,sys,datetime
from cStringIO import StringIO

def shutilcopy2_safe( srcfile  , dest ):
	try:
		shutil.copy2(srcfile, dest)
	except:
		os.chmod(srcfile, 0777)
		shutil.copy2(srcfile, dest)
	finally:
		print '##########' + srcfile
		print '##########' + dest

def copy_folder_rec ( src , dest , extention , out_put=False):# '.pdb,.lib,.exp,.ink'  delimited by ','
	ext_list = extention.split(',')
	for i in os.listdir(src):
		x = os.path.join(src, i)
		if os.path.isdir(x):
			copy_folder_rec(x , os.path.join(dest, i), extention , out_put )
		else:	
			for ext in ext_list:
				if '*' in extention:
					if not os.path.exists( dest ):
						os.makedirs( dest )
					if not os.path.exists( os.path.join(dest, x[x.rfind('\\')+1:] )):# file not exists
						if out_put:
							print '* -> '+x + ' to ' + dest
						shutilcopy2_safe(x, dest)
					else:
						print 'ALREADY EXISTS , so overwriting '+x[x.rfind('\\')+1:]
						shutilcopy2_safe(x, dest)
						
				elif x.endswith(ext):
					if not os.path.exists( dest ):
						os.makedirs( dest )
					if not os.path.exists( os.path.join(dest, x[x.rfind('\\')+1:] )):# file not exists
						if out_put:
							print ' '+x + ' to ' + dest
						shutilcopy2_safe(x, dest)
					else:
						print 'ALREADY EXISTS so overwriting '+x[x.rfind('\\')+1:]
						shutilcopy2_safe(x, dest)

	
#Start of Main()
if _name_ == '_main_':
	try:
		pwd = sys.argv[1]
	except:
		pwd = os.getcwd()
	dest = os.path.join ( pwd , "All_Folders" )
	if not os.path.exists( dest ) :
		os.mkdir ( dest )
	list_files = os.listdir( pwd )
	
	
	old_stdout = sys.stdout
	sys.stdout = mystdout = StringIO()
	###################### disabled the stdout #########################
	
	for i in list_files:
		if os.path.isdir(i):#dir 
			if i != "All_Folders":
					print i + " Fto " + os.path.join( dest , i )
					if not os.path.exists ( os.path.join( dest , i ) ):
						shutil.copytree (  os.path.join( pwd , i ) , os.path.join( dest , i ) )
						shutil.rmtree ( os.path.join( pwd , i )  )
					else:# if the dest folder exists 
						copy_folder_rec( os.path.join( pwd , i )  , os.path.join( dest , i ) , '*' )
						shutil.rmtree ( os.path.join( pwd , i )  )
						
		else:#file 
			extention = i.split('.')[-1]
			if extention == i : # no extention
				ext_folder = "Others_Folder"
				print i + " to " + os.path.join( dest , ext_folder )
				if not os.path.exists( os.path.join( dest , ext_folder ) ) :
					os.mkdir ( os.path.join( dest , ext_folder ) )
				shutil.move ( os.path.join( pwd , i ) , os.path.join( dest , ext_folder ) )
			elif i in _file_ :
				print i
				pass
			else:
				ext_folder = extention+"_Folder"
				print i + " to " + os.path.join( dest , ext_folder )
				if not os.path.exists( os.path.join( dest , ext_folder ) ) :
					os.mkdir ( os.path.join( dest , ext_folder ) )
				try:	
					shutil.move (  os.path.join( pwd , i ) , os.path.join( dest , ext_folder ) )
				except:
					try:
						os.remove (  os.path.join( pwd , i )  )
						print 'overwriting '+ os.path.join( pwd , i ) 
						shutil.move (  os.path.join( pwd , i ) , os.path.join( dest , ext_folder ) )
					except:
						pass
				
					
					
	sys.stdout = old_stdout
	#####################  enabled the stdout ##########################
	real_output =  mystdout.getvalue()
	print real_output
	
	
	now = datetime.datetime.now()
	filename =  os.environ['USERPROFILE']+"\\ARRANGE_FOLDER_"+ now.strftime("%Y-%m-%d-%H_%M")+"_log.txt" 
	try:
		fd = open ( filename , "w+")
		fd.write ( real_output )
		fd.close()
		print "Logs are saved in " + filename
	except:
		print "Error in opening file " + filename