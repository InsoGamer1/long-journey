#with implementation

import datetime
import os,sys

class LogPrint():
    def __init__(self,fname=None):
        now = datetime.datetime.now()
        fname = "temp_"+ now.strftime("%Y-%m-%d")+"_log.txt"
        if os.name == 'nt': #windows
            self.filename = os.environ['USERPROFILE']
        else:
            self.filename = "/tmp/"+os.environ['USER']
        if not os.path.exists( self.filename ):
            os.makedirs( self.filename )
        self.filename = os.path.join( self.filename , fname )
        self.new_fd = open( self.filename , 'a+' )
        self.buff = ""
        
    def __enter__(self):
        self.backup_fd = sys.stdout #taking backup of STDOUT
        sys.stdout = self.new_fd #replacing the STDOUT with the temp file created
        
        
    def __exit__(self, type, value, traceback):
        sys.stdout = self.backup_fd #replacing back the actual STDOUT to STDOUT
        #read the console output from the file
        self.new_fd.close()
        print "check this %s for output"%self.filename
        
with LogPrint() as _:
    print "this will not be printed"
    
print "this wil be printed "
