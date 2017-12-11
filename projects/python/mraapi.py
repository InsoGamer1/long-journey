"""
#Author : Mujtaba Raheman
A base class for all stuff related to
DB opertation , mail operation , importing operation 
String operation , html formatting , print formating
and some of the file operation
"""
import sys,os,datetime,time

def import_module( module_name ,package=None , fromself=0 ):#requires os,sys modules
	"""
		Usage: 
			for ex: import psycopg2 
			use : psycopg2 = import_module( "psycopg2" )
			for ex: from email.MIMEBase import MIMEBase" 
			use : MIMEBase = import_module( "MIMEBase" , "email.MIMEBase")
	"""
        if module_name in sys.modules:#checking if already imported
                return sys.modules[module_name]
	try:#import
		if package:
			return getattr(__import__(package, fromlist=[module_name]), module_name)
		else:
			return __import__(module_name,package)
	except:#download and install and import
		print "importing .........." ,module_name
		if os.name == 'nt': #windows 
			getpipe = r'\\<WINDOWS_PATH>\Raheman\get-pip.py'
			piplocation = os.path.join( os.path.dirname(sys.executable) , r"Scripts\pip.exe" )
		else:#linux
			getpipe = r'<LINUX_PATH>/Raheman/get-pip.py'
			piplocation = "/usr/local/bin/pip"
		
		if os.path.exists( piplocation ):
			if not os.system(piplocation +" install "+module_name ):#sucess
				if package:
					return getattr(__import__(package, fromlist=[module_name]), module_name)
				else:
					return __import__(module_name,package)
			else:
                                print "Please check the module name or module may need to be installed externally"
		elif fromself==0:#func is not called recursively and pip is not found
			print "pip is not installed on machine"
			print "downloading and installing pip on machine"
			
			if not os.system("python "+getpipe ):#sucess
				print "pip is installed correctly!!"
				return import_module( module_name ,package , 1 )
			else:
				print "Error in installing pip "
				sys.exit()
		else:#recursively call , no more try to install pip
			print "Error in installing pip "
			sys.exit()	
	print "ImportError :" , module_name
	sys.exit()
	
pyodbc = import_module( "pyodbc" )
pymssql = import_module( "pymssql" )
pymysql = import_module( "pymysql" )
psycopg2 = import_module( "psycopg2" )
smtplib = import_module( "smtplib" )
subprocess = import_module( "subprocess" )
MIMEBase = import_module( "MIMEBase" , "email.MIMEBase")#from email.MIMEBase import MIMEBase" 
import shutil
import random
import base64

reload(sys)
sys.setdefaultencoding('latin-1')
#sys.setdefaultencoding('utf-8')
#sys.setdefaultencoding('ascii')

MIMEText = import_module( "MIMEText" , "email.mime.text")
MIMEMultipart = import_module( "MIMEMultipart" , "email.MIMEMultipart")
MIMEApplication = import_module( "MIMEApplication" , "email.mime.application")
basename = import_module( "basename" , "os.path")#from os.path import basename
myPswd = ""
with open(os.path.join( os.path.dirname(sys.executable) , r"Scripts\myPswd" ) , "r" ) as f:
	myPswd = f.read()
	
def dump_args(func):
    "This decorator dumps out the arguments passed to a function before calling it"
    argnames = func.func_code.co_varnames[:func.func_code.co_argcount]
    fname = func.func_name
    def echo_func(*args,**kwargs):
        print fname, "(", ', '.join(
            '%s=%r' % entry
            for entry in zip(argnames,args[:len(argnames)])+[("args",list(args[len(argnames):]))]+[("kwargs",kwargs)]) +")"
    return echo_func

def time_taken(some_function):
	def wrapper(*args, **kwargs):
		t1 = time.time()
		res = some_function(*args, **kwargs)
		t2 = time.time()
		print 'Time taken for %s : %2.6f sec' % (some_function.__name__, t2-t1)
		return res
	return wrapper
	
def mra_checkDbResultStrictly(some_function):
	def wrapper(*args, **kwargs):
		status ,res  = some_function(*args, **kwargs)
		if status == -1 : #error 
			print "Error in Query " , kwargs 
			print "Message " , res 
			sys.exit()
		else:
			return res 
	return wrapper
	
def mra_checkDbResult(some_function):
	def wrapper(*args, **kwargs):
		print args
		print kwargs
		status ,res  = some_function(*args, **kwargs)
		if status == -1 : #error 
			print "Error in Query " , query 
			print "Message " , res 
			return []
		else:
			return res 
	return wrapper
	
def cmd_execute( cmd_in):
	try:
		print "\nExecuting  " + cmd_in
		_cmd_out = subprocess.Popen(cmd_in, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
		(stdout_return, stderr_return) = _cmd_out.communicate()
		return stdout_return.decode('ascii', 'ignore')
	except WindowsError as e:
		print cmd_in + " couldnt be executed! Check that you can run the command\n" + str(e)
		

def send_mail( subject , msgbody , tolist=['ahad.r4u@gmail.com'] ,fromUser='ahad.r4u@gmail.com' , Footer=0 , mimetype='html' , ccUser=[] , attachments = []):
	footer = '<b><i>Thanks & Regards<br>'
	footer += 'M.Raheman</i><b>'
	if Footer:
		msgbody = msgbody +'<br>' +footer
	
	msg = MIMEMultipart()
	msg['From'] = fromUser
	
	for i in range(len(tolist)):
		if '@' not in tolist[i]:
			tolist[i] += "@gmail"

	if ccUser!=[]:
		for i in range(len(ccUser)):
			if '@' not in ccUser[i]:
				ccUser[i] += "@gmail"
		msg['Cc'] = "; ".join(ccUser)
		
	msg['To'] = "; ".join(tolist)
	msg['Subject'] = subject
	msg.attach(MIMEText(msgbody, mimetype))
	
	for f in attachments:
                if os.path.exists(f):
                        with open(f, "rb") as fil:
                                part = MIMEApplication(	fil.read(),Name=basename(f))
                                part['Content-Disposition'] = 'attachment; filename="%s"' % basename(f)
                                msg.attach(part)
                else:
                        print "Invalid attachment path " , f
	
	print 'sending mail...'
	print 'From : ' + msg['From']
	print 'To : ' + msg['To']
	print 'Subject : '+ msg['Subject']
	
	if msg['To'] == "":
		print "Unable to send mail , error in To User"
		return False
	
	smtpObj = smtplib.SMTP('<SMPT_HOST>')
	smtpObj.sendmail(fromUser, tolist , msg.as_string())
	smtpObj.quit()
	print 'Mail sent!!!'
	return True

def checkpath( src ):
	if os.path.exists(src):
		return True
	else:
		return False
def safe_delete_rec ( folder ):
	if not os.path.exists( folder ):
		return 
	try:
		shutil.rmtree ( os.path.join( pwd , i )  )
	except:
		for file_or_folder in os.listdir(folder):
			x = os.path.join(folder, file_or_folder)
			if os.path.isdir(x):#folder 
				it_is_folder = x 
				safe_delete_rec(it_is_folder)
			else:	#file 
				it_is_file = x 
				try:
					os.unlink(it_is_file)
				except:
					os.chmod(it_is_file, 0777)
					os.unlink(it_is_file)
				finally:
					print "error in deleting " , it_is_file
def del_files_in_folder ( src , extention):# '.pdb,.lib,.exp,.ink'  delimited by ','
	ext_list = extention.split(',')
	for i in os.listdir(src):
		x = os.path.join(src, i)
		if os.path.isdir(x):
			del_files_in_folder(x , extention)
		else:	
			for ext in ext_list:
				if x.endswith(ext):
					print x
					os.remove(x)
def copy_only_file_from_folder ( src , dest , extention , out_put):# '.pdb,.lib,.exp,.ink'  delimited by ','
	if not os.path.exists( src ):
		print 'Source doesnt exists ' + src 
		return
	ext_list = extention.split(',')
	if not os.path.exists( dest ):
		os.makedirs( dest )
	for i in os.listdir(src):
		x = os.path.join(src, i)
		if not os.path.isdir(x):# only files 
			for ext in ext_list:
				if '*' in extention:
					if out_put:
						print '* -> '+ x + ' to ' + dest
					shutil.copy2(x, dest)
				elif x.endswith(ext):
					if out_put:
						print ' '+ x + ' to ' + dest
					shutil.copy2(x, dest)
		else:
			print 'Skipping directory ' + x	
def shutilcopy2_safe( srcfile  , dest ):
	try:
		shutil.copy2(srcfile, dest)
	except:
		os.chmod(srcfile, 0200)
		if not os.path.isdir(dest):
			os.chmod(dest, 0200)
		shutil.copy2(srcfile, dest)
	finally:
		print '##########' + srcfile
		print '##########' + dest
def copy_folder_rec ( src , dest , extention , out_put=False):# '.pdb,.lib,.exp,.ink'  delimited by ','
	if '*' in extention:
		for i in os.listdir(src):
			x = os.path.join(src, i)
			if os.path.isdir(x):#folder 
				copy_folder_rec(x , os.path.join(dest, i), extention , out_put )
			else:	#file 
				filename = os.path.basename(x)
				if not os.path.exists( dest ):
					os.makedirs( dest )
				if not os.path.exists( os.path.join(dest, filename )):# file not exists
					if out_put:
						print '* -> '+x + ' to ' + dest
					shutilcopy2_safe(x, dest)
				else:
					print 'ALREADY EXISTS , so overwriting '+filename
					shutilcopy2_safe(x, os.path.join( dest , filename ))
	else:
		ext_list = extention.split(',')
		for i in os.listdir(src):
			x = os.path.join(src, i)
			if os.path.isdir(x):#folder 
				copy_folder_rec(x , os.path.join(dest, i), extention , out_put )
			else:#file 
				filename = os.path.basename(x)
				for ext in ext_list:
					if filename.endswith(ext):
						if not os.path.exists( dest ):
							os.makedirs( dest )
						if not os.path.exists( os.path.join(dest, filename )):# file not exists
							if out_put:
								print ' '+x + ' to ' + dest
							shutilcopy2_safe(x, dest)
						else:
							print 'ALREADY EXISTS so overwriting '+filename
							shutilcopy2_safe(x, os.path.join( dest , filename ))
						break
def copy_only_this ( src , dest , extention ):
	if not os.path.exists( src ):
		print 'Source doesnt exists ' + src 
		return
	if not os.path.exists( dest ):
		os.makedirs( dest )
	ext_list = extention.split(',')
	list = os.listdir(src)
        for filename in list:
		if '*' in extention:
			fullfilename = os.path.join(src, filename)
			print '   '+fullfilename,dest
			shutil.copy2(fullfilename, dest)
		else:
			for ext in ext_list:
				if filename.endswith(ext):
					fullfilename = os.path.join(src, filename)
					print '   '+fullfilename,dest
					shutil.copy2(fullfilename, dest)
def tuplelist_to_table(): #table form 
	return [list(x) for x in list_of_tuples] 
def tuplelist_to_dict(list_of_tuples , key_index): #dict[key]=list( row values )
	return {x[key_index]:list(x) for x in list_of_tuples}#key:value i.e dictionary of {1st col of nth row :list of nth row}
def get_dict_from_table( table , key_index):
	dict = {}
	for i in table:
		if dict.has_key(i[key_index]):
			dict[i[key_index]] += [i]
		else:
			dict[i[key_index]] = [i]
	return dict
	
def comm_list ( listA , listB ) :# A(''')B
	return list(set.intersection( set(listA) , set(listB) ))
	#OR
	list_comm = []
	for i in listA:
		for j in listB:
			if i==j:
				list_comm.append ( i )
	return list_comm
def union_list( listA , listB ) :# A U B
	return list( set( listA + listB ))
	#OR
	return list(set.union( set(listA) , set(listB) ))
def diff_list ( listA , listB ):# A-B
	return list(set(listA) - set(listB) )
	#OR
	list_diff = []
	for i in listA:
		if not i in listB:
			list_diff.append ( i )
	return list_diff	
def dict_to_html( dict ):		
	print '<table border="1" >'
	for i in dict:
		rowspan = len(dict[i])
		print '<tr><td rowspan="'+str(rowspan)+'">'+i+'</td>'
		for j in range( len(dict[i])):
			for k in range(len(dict[i][j])):
				print  "<td>"+str(dict[i][j][k])+"</td>"
			print "</tr>"
	print '</table>'

class MyEncoding(object):
	def __init__(self ):
		pass
	def encode( self ,data ):
		return base64.b64encode(data)
	def decode(self , en_data ):
		return base64.b64decode( en_data )
	def create_encoded_file( self ,data , filename=None ):
		if not filename:
			filename = "encrypted_output.enc"
		else:
			filename +=  ".enc"
		try:
			with open ( filename , "w+") as fd:
				fd.write( self.encode( data ) )
				print "File created : " , os.path.join(os.getcwd(),filename)
				return os.path.join(os.getcwd(),filename)
		
		except:
			print "Error in creating file " , filename
			return None
	def decode_file( self ,filename ):
		if filename:
			try:
				with open ( filename , "r") as fd:
					return base64.b64decode(fd.read())
			except:
				print "Error in reading file " , filename
				return None
		else:
			print "Error in filename " , filename
			return None
		
class BigChar(object):
	def __init__(self, p='#' , d=' '):
		self.size_g = 5
		self.thickness_g = 1
		self.p = p
		self.d = d
		self.pattern_g = { 	
			'A' : [[2],[1,3] , [0,1,2,3,4] ,[0,4] , [0,4]] ,
			'B' : [[0,1,2,3],[0,4] , [0,1,2,3] ,[0,4] , [0,1,2,3]] ,
			'C' : [[1,2,3,4],[0] , [0] ,[0] , [1,2,3,4]] ,
			'D' : [[0,1,2,3],[0,4] , [0,4] ,[0,4] , [0,1,2,3]] ,
			'E' : [[0,1,2,3,4],[0] , [0,1,2,3] ,[0] , [0,1,2,3,4]] ,
			'F' : [[0,1,2,3,4],[0] , [0,1,2,3] ,[0] , [0]] ,
			'G' : [[1,2,3,4],[0] , [0,2,3,4] ,[0,4] , [1,2,3,4]] ,
			'H' : [[0,4],[0,4] , [0,1,2,3,4] ,[0,4] , [0,4]] ,
			'I' : [[0,1,2,3,4],[2] , [2] ,[2] , [0,1,2,3,4]] ,
			'J' : [[0,1,2,3,4],[3] , [3] ,[0,3] , [0,1,2,3]] ,
			'K' : [[0,4],[0,3] , [0,1,2] ,[0,3] , [0,4]] ,
			'L' : [[0],[0] , [0] ,[0] , [0,1,2,3,4]] ,
			'M' : [[0,4],[0,1,3,4] , [0,2,4] ,[0,4] , [0,4]] ,
			'N' : [[0,4],[0,1,4] , [0,2,4] ,[0,3,4] , [0,4]] ,
			'O' : [[1,2,3],[0,4] , [0,4] ,[0,4] , [1,2,3]] ,
			'P' : [[0,1,2,3],[0,4] , [0,1,2,3] ,[0] , [0]] ,
			'Q' : [[1,2,3],[0,4] , [0,2,4] ,[0,3] , [1,2,4]] ,
			'R' : [[0,1,2,3],[0,4] , [0,1,2,3] ,[0,3] , [0,4]] ,
			'S' : [[0,1,2,3,4],[0] , [0,1,2,3,4] ,[4] , [0,1,2,3,4]] ,
			'T' : [[0,1,2,3,4],[2] , [2] ,[2] , [2]] ,
			'U' : [[0,4],[0,4] , [0,4] ,[0,4] , [1,2,3]] ,
			'V' : [[0,4],[0,4] , [0,4] ,[1,3] , [2]] ,
			'W' : [[0,4],[0,4], [0,2,4] ,[0,1,3,4], [0,4]] ,
			'X' : [[0,4],[1,3], [2] ,[1,3], [0,4]] ,
			'Y' : [[0,4],[1,3], [2] ,[2], [2]] ,
			'Z' : [[0,1,2,3,4],[3] , [2] ,[1] , [0,1,2,3,4]] ,
			'a' : [[0,1,2,3],[3] , [0,1,2,3] ,[0,3] , [0,1,2,3]],
			'b' : [[0],[0] , [0,1,2,3,4] ,[0,4] , [0,1,2,3,4]] ,
			'c' : [[],[] , [0,1,2,3,4] ,[0] , [0,1,2,3,4]] ,
			'd' : [[4],[4] , [0,1,2,3,4] ,[0,4] , [0,1,2,3,4]] ,
			'e' : [[],[0,1,2,3,4] , [0,4] ,[0] , [0,1,2,3,4]] ,
			' ' : [[],[],[],[] ,[]],
			':' : [[],[2,3],[],[2,3] ,[]],
			',' : [[],[],[],[1] ,[0]],
			'.' : [[],[],[],[0,1] ,[0,1]],
			'"' : [[2,3],[2,3],[],[] ,[]],
			'!' : [[1,2],[1,2],[1,2],[] ,[1,2]],
			'1' : [[2],[1,2],[2],[2] ,[0,1,2,3,4]],
			'2' : [[0,1,2,3,4],[4],[0,1,2,3,4],[0] ,[0,1,2,3,4]],
			'3' : [[0,1,2,3,4],[4],[0,1,2,3,4],[4] ,[0,1,2,3,4]],
			'4' : [[0,4],[0,4],[0,1,2,3,4],[4] ,[4]],
			'5' : [[0,1,2,3,4],[0],[0,1,2,3,4],[4] ,[0,1,2,3,4]],
			'6' : [[0,1,2,3,4],[0],[0,1,2,3,4],[0,4] ,[0,1,2,3,4]],
			'7' : [[0,1,2,3,4],[4],[4],[4] ,[4]],
			'8' : [[0,1,2,3,4],[0,4],[0,1,2,3,4],[0,4] ,[0,1,2,3,4]],
			'9' : [[0,1,2,3,4],[0,4],[0,1,2,3,4],[4] ,[0,1,2,3,4]],
			'0' : [[0,1,2,3,4],[0,3,4],[0,2,4],[0,1,4] ,[0,1,2,3,4]],
			'None' : [[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4],[0,1,2,3,4]]
		}
	def create_template( self,d=' '): return [d for x in range(self.size_g ) ]
	def printChar( self, patternOfCharWithPos,p,d):
		temp = self.create_template(d)
		for codeI in patternOfCharWithPos:
			temp[codeI] = p
		return temp
	def prints(self, string , p="NA" , d="NA"):
		if p=="NA":
			p = self.p
		if d=="NA":
			d = self.d
		string = str(string)
		for pos in range(self.size_g):
			temp = []
			for ch in string:
				ch = ch.upper()
				if self.pattern_g.has_key(ch):
					temp +=self.printChar( self.pattern_g[ch][pos] , p , d )
				else:
					temp +=self.printChar( self.pattern_g['None'][pos] , ch ,d )
				temp+=[' ']
			print "".join( temp )
			
class BuffIO(object):
	"""
		buff = BuffIO()
		buff.startCapturing()
		#......your code here .....
		buff.stopCapturing()
		print buff.getBuffer()#output : hello world!
	"""
	def __init__(self, dont_del=0 , filename=None):#filename should be absolute path if not, filename is considered to be in current directory
		self.filename = filename
		if not self.filename:#if filename not specified , temp file will be created
			now = datetime.datetime.now()
			logfile = "temp_"+ now.strftime("%Y-%m-%d")+"_log.txt"
			if os.name == 'nt': #windows
				self.filename = os.environ['USERPROFILE']
			else:
				self.filename = "/tmp/"+os.environ['USER']
				if not os.path.exists( self.filename ):
					os.makedirs( self.filename )
			self.filename = os.path.join( self.filename , logfile )
		
		self.new_fd = open( self.filename , 'w+' )
		self.buff = ""
		self.dont_del = dont_del
		self.os = os # bcoz by the time control reaches __del__ , os module will removed , so saving as class member
		self.startCapturing()

	def startCapturing(self):
		self.backup_fd = sys.stdout #taking backup of STDOUT
		sys.stdout = self.new_fd #replacing the STDOUT with the temp file created
		
	def stopCapturing(self):
		sys.stdout = self.backup_fd #replacing back the actual STDOUT to STDOUT
		#read the console output from the file
		self.new_fd.close()
		self.new_fd1 = open( self.filename , 'r' )
		self.buff = self.new_fd1.read()# storing in buffer
		self.new_fd1.close()
		
	def getBuffer(self):
		self.stopCapturing()
		return self.buff
				
	def __del__(self):
		if self.dont_del:# if dont_del is set , temp will not be deleted
			print "log saved as " , self.filename
		else:
			self.os.unlink( self.filename )
		


class psgsql_api(object):
	def __init__( self ,host="SERVER" ,db='DBNAME', domain_user="USER",passwd="PASSWORD" ):
		self.hostname = host
		self.username = domain_user
		self.password = passwd
		self.database = db
		self.myConnection = psycopg2.connect( host=self.hostname, user=self.username, password=self.password, dbname=self.database )
		self.cursor = self.myConnection.cursor()
	
	def execute_query (self, query , col=0):
		try:
			self.cursor.execute (query)
			if query.upper().startswith("INSERT"):
				return ( 1 , self.conn2.rowcount )
			return self.cursor.fetchall ()
			list_of_tuples = self.cursor.fetchall ()
			returnList = []
			if col:
				list_of_cols = self.cursor.description
				returnList.append( [desc[0] for desc in list_of_cols] )
			returnList += [list(x) for x in list_of_tuples]
			return (1 , returnList)
			#return {x[0]:"" for x in list_of_tuples}
		except Exception,e:
			if ( len(e.args ) != 0 ):
				return (-1 , str( e.args ))
			else:
				return (-1 ,"UNKNOWN_ERROR")
	
	def __deinit__( self ):
		self.myConnection.close()
		
class mysql_api(object):
	def __init__(self, host="SERVER" ,db='DBNAME', domain_user="USER",passwd="PASSWORD" , autocommit=True):#host,db,user,password
		self.host = host 
		self.db = db 
		self.domain_user = domain_user 
		self.password = passwd
		self.autocommit = autocommit
		self.conn = pymysql.connect(self.host, self.domain_user, self.password, self.db , use_unicode=False , connect_timeout=1 )
		self.conn.autocommit(autocommit)
		self.cursor = self.conn.cursor(pymysql.cursors.DictCursor)
		self.retryCount = 1
		self.lastResult = None
		if not (self.cursor):
			print "Error in creating connection"
			sys.exit()
		#print 'Connected to DB  :'+db
	
	def reinit(self,query):
		print "****************** Reinitiliazing MySQL connection... ******************************"
		self.retryCount = 1
		self.conn = pymysql.connect(self.host, self.domain_user, self.password, self.db , use_unicode=False )
		self.conn.autocommit(self.autocommit)
		self.cursor = self.conn.cursor(pymysql.cursors.DictCursor)
		if not (self.cursor):
			print "Error in creating connection"
			sys.exit()
		return self.execute_query (query)
		#print 'Connected to DB  :'+db
	
	def execute_query (self, query):
		"""
			result = "( {....} , {...})"
			USAGE: 
				for rownum in range( len(result)):
					print "\n".join( str(res[rownum][columnName]) for columnName in res[rownum])
		"""
		try:
			self.cursor.execute (query)
			self.lastResult = None
			if query.upper().startswith("INSERT"):
				self.lastResult = ( 1 , self.cursor.lastrowid )
				return self.lastResult
			list_of_tuples = {}
			self.lastResult = (1 , self.cursor.fetchall ())
			return self.lastResult
			#return {x[0]:"" for x in list_of_tuples}
		except Exception,e:
			print "******** MySQL CLASS :" , e.__class__  , "******************"
			if ( e.__class__ == pymysql.err.Error or e.__class__ == pymysql.err.DatabaseError or 'connection was aborted by the software' in str( e.args )) and self.retryCount:
				self.lastResult =  self.reinit(query)
                        elif e.__class__ == pymysql.err.IntegrityError:
				print e
                                self.lastResult = (1 , [] )
			elif ( len(e.args ) != 0 ):
				self.lastResult = (-1 , str( e.args ))
			else:
				self.lastResult = (-1 ,"UNKNOWN_ERROR")
			return self.lastResult
	def get_cols(self):
		return [desc[0] for desc in self.cursor.description]
		
	def affected_rows(self):
		return self.conn.affected_rows()
	def __repr__(self ):
                ret = ""
		result = self.lastResult
		if result:
                        if len(result) == 2 : # with status
                                if result[0]==1:
                                        ret += "\nQuery status :Passed\n"
                                        result = result[1]
                                else:
                                        ret +=  "\nQuery status :Failed\n"
                                        ret +=  "Reason : %s" %(result[1])
                                        return ret
			lenList = self.get_cols()
			lenDict = { col:len(col) for col in lenList}
                        ret += "\t|\t".join( lenList ) + "\n"
                        #ret +=  "\n".join( [ "\t".join( str(row[col])+" "*(lenList[i]-len(str(row[col])))  for col in row ) for row in result] )
                        ret +=  "\n".join( [ "\t|\t".join(	("%-{0}s".format(lenDict[col]))%str(row[col])	for col in lenList ) for row in result] )
		return ret
	def commit(self):
		self.conn.commit()
		
	def rollback(self):
		self.conn.rollback()
		
	def close( self ):
		self.__deinit__()
		
	def __deinit__( self ):
		self.conn.close()
		
		
	
class p4_api(object):
	def __init__( self , p4user="USER" , p4port='P4SERVER:PORT' , p4pass=myPswd , p4client='CLIENT_SPEC'):
		self.p4user = p4user
		self.p4port = p4port
		self.p4pass = p4pass
		self.p4client = p4client
		login_cmd = "p4 -p %s -u %s login"%(self.p4port, self.p4user)
		tempfd = open( 'tempPass.txt' , 'w' )
		tempfd.write(self.p4pass)
		tempfd.seek(0)
		tempfd.close()
		my_proc = subprocess.Popen(login_cmd.split(' '), stdout=subprocess.PIPE, stdin=open( 'tempPass.txt'))
		output, error = my_proc.communicate()
		if 'logged in' in output:
			print output
			os.environ['P4USER']=self.p4user
			os.environ['P4PORT']=self.p4port
			if self.p4client:
				os.environ['P4CLIENT']=self.p4client
		else:
			print "Error in login to p4"
			sys.exit()
		
	def __deinit__(self):
		logout_cmd = "p4 logout"
		my_proc = subprocess.Popen(logout_cmd.split(' '), stdout=subprocess.PIPE)
		output, error = my_proc.communicate()
		if not 'logged out' in output:
			print "Error in login to p4"
		else:
			print "Logged out successfully !!!"
	
	
class pyodbc_api(object):
	def __init__(self, host='SERVER' ,db='DBNAME', domain_user="USER",password="PASSWORD" , Trusted_Connection="yes"):#host,db,user,password
		"""
		self.conn = pyodbc.connect(r'Driver={SQL Server};Server='+self.host+';Database='+self.db+';Trusted_Connection='+self.Trusted_Connection+';UID='+self.domain_user+';PWD='+self.password+'' , autocommit=True)
		"""
		self.host = host 
		self.db = db 
		self.domain_user = domain_user 
		self.password = password
		self.Trusted_Connection = Trusted_Connection
		connection_string = r'Driver={SQL Server};Server='+self.host+';Database='+self.db+';Trusted_Connection='+self.Trusted_Connection+';UID='+self.domain_user+';PWD='+self.password+''
		self.conn = pyodbc.connect(connection_string, autocommit=True)
		self.retryCount = 1
		self.lastResult = None
		self.cursor = self.conn.cursor()
		if not (self.cursor):
			print "Error in creating pyodbc connection"
			sys.exit()
		#print 'Connected to DB  :'+db
		
	def reinit(self,query):
		print "****************** Reinitiliazing PYODBC connection... ******************************"
		self.retryCount = 1
		self.conn = pyodbc.connect(r'Driver={SQL Server};Server='+self.host+';Database='+self.db+';Trusted_Connection='+self.Trusted_Connection+';UID='+self.domain_user+';PWD='+self.password+'')
		self.cursor = self.conn.cursor()
		if not (self.cursor):
			print "Error in creating pyodbc connection"
			sys.exit()
		return self.execute_query (query)
	
	def execute_query (self, query):
		try:
			query = query.strip()
			self.cursor.execute (query)
			self.lastResult = None
			if query.upper().startswith("INSERT"):
				self.lastResult = ( 1 , self.cursor.rowcount )
			elif query.upper().startswith("UPDATE"):
				self.lastResult = (1 ,self.cursor.rowcount )
			elif query.upper().startswith("DELETE"):
				self.lastResult = ( 1 , self.cursor.rowcount )
			elif query.upper().startswith("BEGIN"):
				self.lastResult = (1 ,"Updated" )
			else:
				# [ {c1:v1 , c2;v2,...} , {} ..]
				list_of_tuples = self.cursor.fetchall ()
				list_of_cols = [desc[0] for desc in self.cursor.description]
				returnList = [{ colname:colval for colname,colval in zip(list_of_cols,row)} for row in list_of_tuples]
				self.lastResult = (1 ,returnList )
			return self.lastResult
			
		except Exception,e:
			print "******** PYODBC CLASS :" , e.__class__  , "******************"
			#if ('network error' in e.args or 'Communication link failure' in e.args) and self.retryCount:
			if e.__class__ == pyodbc.Error  and self.retryCount:
				self.lastResult = self.reinit(query)
			elif ( len(e.args ) != 0 ):
				self.lastResult = (-1 , str( e.args ))
			else:
				self.lastResult = (-1 ,"UNKNOWN_ERROR")
			return self.lastResult
	def get_col(self):
		return [i[0] for i in self.cursor.description]
	def __repr__(self ):
                ret = ""
		result = self.lastResult
		if result:
                        if len(result) == 2 : # with status
                                if result[0]==1:
                                        ret += "Query status :Passed\n"
                                        result = result[1]
                                else:
                                        ret +=  "Query status :Failed\n"
                                        ret +=  "Reason : %s" %(result[1])
                                        return ret
			lenList = self.get_col()
			lenDict = { col:len(col) for col in self.get_col()}
                        ret += "\t|\t".join( self.get_col() ) + "\n"
                        #ret +=  "\n".join( [ "\t".join( str(row[col])+" "*(lenList[i]-len(str(row[col])))  for col in row ) for row in result] )
                        ret +=  "\n".join( [ "\t|\t".join(	("%-{0}s".format(lenDict[col]))%str(row[col])	for col in self.get_col() ) for row in result] )
		return ret
	def __deinit__( self ):
		self.conn.close()
				
class mssql_api(object):
	def __init__(self, host='SERVER' ,db='DBNAME', domain_user="USER",password="PASSWORD"):#host,db,user,password
		self.host = host 
		self.db = db 
		self.domain_user = domain_user 
		self.password = password 
		self.conn = pymssql.connect(self.host, self.domain_user, self.password , self.db )
		self.conn.autocommit(True)
		self.cursor = self.conn.cursor(as_dict=True)
		self.retryCount = 1
		self.lastResult = None
		if not (self.cursor):
			print "Error in creating connection"
			sys.exit()
		#print 'Connected to DB  :'+db
	
	def reinit(self,query):
		print "****************** Reinitiliazing MSSQL connection... ******************************"
		self.retryCount = 1
		self.conn = pymssql.connect(self.host, self.domain_user, self.password , self.db)
		self.conn.autocommit(True)
		self.cursor = self.conn.cursor(as_dict=True)
		if not (self.cursor):
			print "Error in creating connection"
			sys.exit()
		return self.execute_query (query)
	
	
	def execute_query (self, query):
		try:
			query = query.strip()
			self.cursor.execute (query)
                        self.lastResult = None
			if query.strip().upper().startswith("INSERT"):
				self.lastResult = ( 1 , self.cursor.rowcount )
			elif query.strip().upper().startswith("UPDATE"):
				self.lastResult = (1 ,self.cursor.rowcount )
			elif query.upper().startswith("DELETE"):
				self.lastResult = ( 1 , self.cursor.rowcount )
			elif query.upper().startswith("BEGIN"):
				self.lastResult = (1 ,"Updated" )
			else:
				list_of_tuples = self.cursor.fetchall ()
				self.lastResult = (1 ,list_of_tuples )
			return self.lastResult
		except Exception,e:
			if 'Connection reset by peer' in e.message and self.retryCount:
				print "******** MSSQL CLASS :" , e.__class__  , "******************"
				self.lastResult =  self.reinit(query)
			elif ( len(e.args ) != 0 ):
				self.lastResult =  (-1 , str( e.args ))
			else:
				self.lastResult =  (-1 ,"UNKNOWN_ERROR")
			return self.lastResult
	
	def __repr__(self ):
                ret = ""
		result = self.lastResult
		if result:
                        if len(result) == 2 : # with status
                                if result[0]==1:
                                        ret += "Query status :Passed\n"
                                        result = result[1]
                                else:
                                        ret +=  "Query status :Failed\n"
                                        ret +=  "Reason : %s" %(result[1])
                                        return ret
			lenList = self.get_col()
			lenDict = { col:len(col) for col in self.get_col()}
                        ret += "\t|\t".join( self.get_col() ) + "\n"
                        #ret +=  "\n".join( [ "\t".join( str(row[col])+" "*(lenList[i]-len(str(row[col])))  for col in row ) for row in result] )
                        ret +=  "\n".join( [ "\t|\t".join(	("%-{0}s".format(lenDict[col]))%str(row[col])	for col in self.get_col() ) for row in result] )
		return ret
                
	def get_col(self):
		return [i[0] for i in self.cursor.description]
		
	def display ( self , table ):
		for i in table:
			print "\t".join( str(x) for x in i )##
	def display_csv ( self , table , need_col=1):
		buffer = BuffIO()
		buffer.startCapturing()
		if ( need_col == 1 ):
			print ",".join( str(x)  for x in self.get_col ())##
		for i in table:
			print ",".join( str(x) for x in i )##
		buffer.stopCapturing()
		buf = buffer.getBuffer()
		return buf
		
	def display_html ( self , table ,need_col=0):
		buffer = BuffIO()
		buffer.startCapturing()
		print '<table border="1" >'##
		if ( need_col == 1 ):
			print '<tr style="background-color: bisque;">'##
			print "".join('<th>' +  str(x) + '</th>'  for x in self.get_col ())##
			print '</tr>'##
		for i in range(len(table)):
			print '<tr>'##
			for j in range( len(table[i])):
				print '<td>' +  str(table[i][j]) + '</td>'##
			print '</tr>'##
		print '</table>'##
		buffer.stopCapturing()
		buf = buffer.getBuffer()
		return buf
		
	def write_html ( self ,fd ,  table ,need_col , tagname , header , col_list=[] ):
		fd.write('<div id="'+tagname+'" >\n' )
		fd.write('<p>'+header+'&nbsp;&nbsp;&nbsp;<a href="#top" >TOP</a>  </p>\n')
		fd.write('<table border="1" >\n')
		coloums = col_list
		if ( need_col != -1 ):
			if ( need_col == 1 ):
				coloums = self.get_col()
			else:
				coloums = col_list
			fd.write('<tr style="background-color: bisque;">\n')
			for x in coloums:
				fd.write( "".join('<th>' +  str(x) + '</th>\n')  )
			fd.write('</tr>\n')
		
		for i in range(len(table)):
			fd.write('<tr>\n')
			for j in range( len(table[i])):
				fd.write('<td>' +  str(table[i][j]) + '</td>\n')
			fd.write('</tr>\n')
		fd.write('</table>\n')
		fd.write('</div>\n')
		fd.write('<br>\n')
	def __deinit__( self ):
		self.conn.close()
