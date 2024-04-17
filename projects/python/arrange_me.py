import os,shutil,sys,datetime

dict = {}
dict["Documents"] = ['pdf','doc','docx','csv','txt','xls','ppt','pptx']
dict["Musics"] = ['mp3','aac','wma','m4a','ogg']
dict["Videos"] = ['mp4','mkv','avi','webm','flv','3gp','mpg','mpeg','wmv']
dict["Pictures"] = ['ico','png','jpeg','jpg']
dict["Executables"] = ['apk','exe','out','msi']
dict["Archives"] = ['zip','rar','7z','iso']
dict["Scripts"] = ['py','php','cmd','sh','html','js','css','c']
black_list = ["Folders","Documents","Musics","Videos","Pictures","Executables","Archives","Scripts"]
def moved_log (src , dest , logf ):
	if not os.path.exists ( dest ):
		shutil.copytree (  src  , dest )
		shutil.rmtree ( src  )
		logf.write("_D_ "+src + "," + dest+"\n")
		print "_D_ "+src + "," + dest
		
def movef_log (src , dest,logf ):		
	if not os.path.exists( dest ) :
		os.mkdir ( dest )
	shutil.move ( src , dest )
	logf.write ( "_F_ "+src + "," + dest+"\n")
	print "_F_ "+src + "," + dest
		
def get_type(f ):
	for i in dict:
		if f in dict[i]:
			return i
	return "Others"
	
#Start of Main():
if __name__ == '__main__':
	if "USERPROFILE" in os.environ:
		home_dir = os.environ["USERPROFILE"]
	elif "HOME" in os.environ:
		home_dir = os.environ["HOME"]
	elif "TMP" in os.environ:
		home_dir = os.environ["TMP"]
	else:
		home_dir = os.getcwd()
	logfilename = os.path.join(home_dir, "mra_copy_log_dont_delete.txt")
	print ("logfilename:", logfilename)
	logf = open (logfilename, "a+")
	logf.write ( "\nDate : " +str(datetime.date.today())+"\n\n")
	cond = 1 
	if len(sys.argv) == 2:
		pwd = sys.argv[1]
		cond = 0
		if not os.path.exists ( pwd ):
			print 'please input valid directory/folder'
			sys.exit()
	else:
		pwd = os.getcwd()
	dest =  pwd
	
	list_files = os.listdir( pwd )
	if len(list_files)>cond:
		if not os.path.exists( dest ) :
			os.mkdir ( dest )
	else:
		print 'No files/folders to arrange'
	
	
	for i in list_files:
		if os.path.isdir(os.path.join( pwd , i )):
			if i not in black_list:
				moved_log ( os.path.join( pwd , i ) , os.path.join( dest+"\\Folders" , i ) ,logf )
			
		else:#file 
			extention = i.split('.')[-1]
			if extention == i : # no extention
				ext_folder = "Others"
				movef_log ( os.path.join( pwd , i ) , os.path.join( dest , ext_folder ) , logf )
			elif i in __file__ :
				pass
			else:
				ext_folder = get_type ( extention )
				movef_log ( os.path.join( pwd , i ) , os.path.join( dest , ext_folder ) , logf)
	logf.close()
