import urllib2,os
import sys
import time
import types
import androidhelper
import subprocess
from sys import stdout
#import download_print as dp

try:
    droid = androidhelper.Android()
except:
    pass
size_dict = { "Name":0,"Size":1 , "Used":2, "Avail":3, "Use%":4 , "loc":5}

def test_alert_dialog_with_buttons(title, message):
  droid.dialogCreateAlert(title, message)
  droid.dialogSetPositiveButtonText('Yes')
  droid.dialogSetNegativeButtonText('No')
  droid.dialogSetNeutralButtonText('Cancel')
  droid.dialogShow()
  return droid.dialogGetResponse().result

def get_url_size(url,req=None):#bytes
    size = None
    try:
        if not req:
            req = urllib2.urlopen( url )
        if req.info().has_key("Content-Length"):
            fsize = int(req.info()["Content-Length"])
            return int(fsize)#1kb
        else:
            #print "File size is UNKNOWN"
            return -1
    except Exception as e:
        shakeit()
        print "get_url_size", e
        
def get_size(start_path):#bytes
	if os.path.isfile(start_path):
		return os.path.getsize(start_path)
	total_size = 0
	for dirpath, dirnames, filenames in os.walk(start_path):
		for f in filenames:
			fp = os.path.join(dirpath, f)
			total_size += os.path.getsize(fp)
	return total_size

def download1( url , filename ):
	if os.path.exists( filename ):
		print "already exists", filename
		return
	print "downloading ... "  ,filename
	headers = { 'User-Agent' : 'Mozilla/5.0' }
	headers = {}
	req = urllib2.Request(url, None, headers)
	req = urllib2.urlopen( req )
	fsize = get_url_size( url , req )
	issafe ,dsize = check_file_size( filename , fsize )
	if not issafe:
	        if dsize is None:
	            return #unknown or 0 byte
	        print "File : " , filename 
	        print "Size : " , fsize/1024 ,"Mb"
	        print "Disk Available Space : " ,dsize/1024 ,"Mb"
	        say("Not enough to space on disk to download the file")
	        shakeit()
	        shakeit()
	        exit()
	read_size = 0
	chunksize = fsize/100
	print "Downloading- %10d byte -- $$$%% "%fsize + "#"*10
	with open( filename , "wb" ) as songfd:
		# for piece in read_in_chunks(req, chunksize):
		if fsize:
			for i in range(100):
				piece = req.read( chunksize)
				if not piece:
					break
				read_size += chunksize
				stdout.write("\rDownloaded - %10d byte -- %3d%% " % (read_size,i) + "#"*(i/10) )
				stdout.flush()
				#print "\rDownloaded - %10d byte -- %3d%%" % (read_size,i) + "#"*i ,
				#print ""
				songfd.write( piece )
			if fsize-read_size > 0:
				piece = req.read( fsize-read_size)
				if piece:
					read_size += len(piece)
					stdout.write("\rDownloaded - %10d byte -- %3d%% " % (read_size,100) + (10)*"#" )
					stdout.flush()
					songfd.write( piece )
		else:#unknown
			while True:
				piece = req.read( chunksize)
				if not piece:
					break
				read_size += chunksize
				stdout.write("\rDownloaded - %12d byte" % (read_size ) )
				stdout.flush()
				songfd.write( piece )
	if read_size == fsize:
		print "\nSuccessfully downlaoded " , filename
		return True
	else:
		print "Downloaded file is corrupted!!"
		return False
		
def download( url , filename ):
	if os.path.exists( filename ):
		print  "already exists " + filename
		return
	
	try:
	    req = urllib2.urlopen( url )
	    print "downloading ... "  ,filename
	    filesize = get_url_size( url , req )
	    issafe ,dsize = check_file_size( filename , filesize )
	    if not issafe:
	        print "File : " , filename 
	        print "Size : " , filesize/1024 ,"Mb"
	        print "Disk Available Space : " ,dsize/1024 ,"Mb"
	        say("Not enough to space on disk to download the file")
	        shakeit()
	        shakeit()
	        exit()
	    songcontent =  req.read()
	    songfd = open ( filename, 'wb' )
	    songfd.write( songcontent )
	    songfd.close()
	    print "downloaded"
	    shakeit()
	except Exception as e:
    	 print "##########  error" , url ,e
	
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
    try:
        print "_TTS_ :" , something
        droid.ttsSpeak(something)
    except:
        print something
    	  
	
def shakeit():
    try:
	        droid.vibrate()
    except:
	        pass
	
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
	if not filesize or filesize < 1:
		print "File size is UNKNOWN or 0 bytes"
		return None,None
	filesize = filesize/1024#1kb
	drive_info = drive_size(file)
	#print drive_info
	if len(drive_info)>0:
		#print filesize ,"<", drive_info[size_dict["Avail"]]
		dsize = int(drive_info[size_dict["Avail"]])
		if int(filesize) < dsize:
			return True,dsize
		else:
			droid.vibrate()
			return False,dsize
	raise Exception("OSError : Unable to exec df" )
	
def get_links(url,key=""):
    import urllib2
    from bs4 import BeautifulSoup
    mediaList = []
    r  = urllib2.urlopen(url)
    data = r.read()
    soup = BeautifulSoup(data,"html.parser")
    for link in soup.find_all('a'):
        href = link.get('href')
        filename = urllib2.urlparse.unquote(href)
        mediaUrl = href
        if key not in mediaUrl:
            continue
        if ".." not in mediaUrl:
            if url not in href:
                mediaUrl = url + href
            mediaList.append( mediaUrl )
    return mediaList

    
if True:
    '''
    	#print check_file_size( "/storage/emulated/0/xxx.y","512000")
    	#print help(droid.ttsSpeak)
    	#droid.ttsSpeak(droid.recognizeSpeech("check")[1])
    	#exit()
    	print check_file_size( "/storage/6E75-0804/Movies/2","512000" )
    	print drive_size()	
    	print clean_up_helper(512000)
    	pass
    '''
    #dir = '/storage/6E75-0804/Movies/2 Broke Girls/season 6'
    dir = '/storage/emulated/0/Download/SE06'
    url = 'http://www.doomsdayent.com/videos/'
    if not os.path.exists( dir ):
        os.makedirs( dir )
    links = get_links(url,"Royal")
    for link in links[:]:
        print link
        #continue
        filename = link.split('/')[-1]
        filename = urllib2.urlparse.unquote(filename)
        #print filename
        file = os.path.join( dir , filename )
        print file
        #continue
        #print get_url_size(link)
        download1 ( link ,file )
        #break
    print "*"*20
