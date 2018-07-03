import urllib2,os
import sys
import time
import types
import androidhelper
import subprocess

droid = androidhelper.Android()
size_dict = { "Name":0,"Size":1 , "Used":2, "Avail":3, "Use%":4 , "loc":5}

def test_alert_dialog_with_buttons(title, message):
  droid.dialogCreateAlert(title, message)
  droid.dialogSetPositiveButtonText('Yes')
  droid.dialogSetNegativeButtonText('No')
  droid.dialogSetNeutralButtonText('Cancel')
  droid.dialogShow()
  return droid.dialogGetResponse().result
  
def get_size(start_path):#bytes
	if os.path.isfile(start_path):
		return os.path.getsize(start_path)
	total_size = 0
	for dirpath, dirnames, filenames in os.walk(start_path):
		for f in filenames:
			fp = os.path.join(dirpath, f)
			total_size += os.path.getsize(fp)
	return total_size

def download( url , filename ):
	if os.path.exists( filename ):
		print "already exists", filename
		return
	print "downloading ... "  ,filename
	req = urllib2.urlopen( url )
	songcontent =  req.read()
	songfd = open ( filename, 'wb' )
	songfd.write( songcontent )
	songfd.close()
	print "downloaded"
	droid.vibrate()
	
def clean_up_helper(filesize,dir="/storage/6E75-0804/Movies"):
	filesize = int(filesize)
	deleted_size = 0
	butStillDelete = True
	for i in os.listdir( dir ):
		if deleted_size >= filesize and butStillDelete:
			say("Space is now available to continue your download")
			say("Do you still want to continue deleting files")
			response = droid.recognizeSpeech("yes")[1]
			if response:
				response = response.lower()
				if "yes" in response:
					butStillDelete = False
			else:
				return True
		say( "Do you want to delete "+i )
		response = droid.recognizeSpeech("yes")[1]
		if response:
			response = response.lower()
			print response
			if "yes" in response:
				say ( "Deleting " + i )
				statinfo  = os.stat(os.path.join( dir , i ))
				f_size = get_size(os.path.join( dir , i ))/1024#kb
				deleted_size += f_size
				print i ,  f_size , deleted_size
				if f_size > 1024:
					say("freed "+str(f_size/1024)+"MB")
				else:
					say("freed "+str(f_size)+"KB")
			elif "no" in response:
				say ( "ok" )
			elif "cancel" in response:
				say ( "ok , going back" )
				return False
			else:
				say("whatever")
		else:
			say("whatever")
	
def say( something ):
	droid.ttsSpeak(something)
	
def drive_size(type="sdcard"):
	if "sdcard" in type or "internal" in type or "emulated" in type:
		key = "data/media"
	else:
		key = "mnt/media"
	storage_report = []
	result = subprocess.check_output(["df"])#1kb
	for line in result.split("\n"):
		if key in line:
			storage_report.append( [x for x in line.split(" ") if x!=''])
	return storage_report[0]
	
def check_file_size( file , filesize ):
	drive_info = drive_size(file)
	print drive_info
	if len(drive_info)>0:
		print filesize ,"<", drive_info[size_dict["Avail"]]
		if int(filesize) < int(drive_info[size_dict["Avail"]]):
			return True
		else:
			droid.vibrate()
			return False
	raise Exception("OSError : Unable to exec df" )
	

if __name__ == "__main__":
	print check_file_size( "/storage/emulated/0/xxx.y","512000")
	print check_file_size( "/storage/6E75-0804/Movies/2","512000" )
	print drive_size()	
	print clean_up_helper(512000)
	pass
	# for i in range( 20,23 ):
		# num =  str(i) if i>9 else "0"+str(i)
		# url = "http://79.127.126.110/Serial/2%20Broke%20Girls/S05/2Broke.Girls.S05E"+num+".480p.mkv"
		# #print url
		# filename = os.path.join ( '/storage/6E75-0804/Movies/2 Broke Girls/season 5' , url.split("/")[-1] )
		# download( url , filename )
