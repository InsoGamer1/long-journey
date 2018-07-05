mport urllib2,os
import sys
import time
import types
import androidhelper
import subprocess
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
  
def get_size(start_path):#bytes
    if os.path.isfile(start_path):
        return os.path.getsize(start_path)
    total_size = 0
    for dirpath, dirnames, filenames in os.walk(start_path):
        for f in filenames:
            fp = os.path.join(dirpath, f)
            total_size += os.path.getsize(fp)
    return total_size

def get_url_file_size(url):#kb
    try:
        site = urllib2.urlopen(url)
        meta = site.info()
        size = int(meta.getheaders("Content-Length")[0])
        size_kb = size / 1024
        return size_kb
    except Exception,e:
        print e

def download( url , filename ):
    filesize = get_url_file_size( url)
    if filesize:
        if check_file_size(file , filesize):
            if os.path.exists( filename ):
                print "already exists", filename
                return
            
            try:
                req = urllib2.urlopen( url )
                print "downloading ... "  ,filename
                songcontent =  req.read()
                songfd = open ( filename, 'wb' )
                songfd.write( songcontent )
                songfd.close()
                print "downloaded"
                try:
                    droid.vibrate()
                except:
                    pass
            except:
                 print "##########  error" , url
        else:
            droid.vibrate()
            droid.vibrate()
            droid.vibrate()
            droid.vibrate()
            print filesize/1024 ,"MB is not available"
            sys.exit(1)
    raise Exception( "UnknownFileSize: , unable to fetch the file size of " , url )
    
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
        if int(filesize) < int(drive_info[size_dict["Avail"]]):
            return True
        else:
            return False
    raise Exception("OSError : Unable to exec df" )
    
def get_links(url):
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
    dir = '/storage/6E75-0804/Movies/Friends/SE11'
    #dir = '/storage/emulated/0/Download/SE07'
    url = 'http://79.127.126.110/Serial/Friends/S11/'
    if not os.path.exists( dir ):
        os.makedirs( dir )
    links = get_links(url)
    for link in links[:]:
        #print link
        filename = link.split('/')[-1]
        print filename
        file = os.path.join( dir , filename )
        #print file
        download ( link ,file )
    print "*"*20