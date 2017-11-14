import os,sys,re,shutil,datetime

def moved (src , dest , logO):
	if not os.path.exists ( dest ):
		shutil.copytree (  src  , dest )
		shutil.rmtree ( src  )
		print "_D_ "+src + "," + dest
		logO.write ( "_D_ "+src + "," + dest +"\n" )
		
def movef (src , dest ,logO):		
	if os.path.exists( src ) :
		if not os.path.exists( dest ) :
			os.mkdir ( dest )
		try:
			shutil.move ( src , dest )
			print "_F_ "+src + "," + dest
			logO.write ( "_F_ "+src + "," + dest +"\n")
		except:
			print "_F_ "+src + "," + dest
			logO.write ( "_F_ "+src + "," + dest+"\n" )
		
def delete_empty_folder ( folder_list ,logO):
	for i in folder_list:
		if os.path.exists(i) and len(os.listdir( i )) == 0:
			try:
				shutil.rmtree ( i  )
				print "Deleted " + str(i)
				logO.write ( "Deleted " + str(i)+"\n" )
			except:
				print 'Not deleting ' + i
				logO.write ( 'Not deleting ' + i +"\n")
			
		
	
	
if __name__ == '__main__':
	if len( sys.argv) == 2:
		date1 = sys.argv[1].strip()
		grp = re.search( '^\d{4}-\d{2}-\d{2}$' , date1)
		if grp:
			logO = open (  "D:\Ahad\\restore_log_dont_delete.txt" , "a+")
			logO.write ( "\nDate : " +str(datetime.date.today())+"\n\n")
			log_file = "D:\Ahad\\copy_log_dont_delete.txt"
			if not os.path.exists(log_file):
				print "log file : " + log_file  + " does not exists!!!"
				sys.exit()
			logf = open (  log_file , "r")
			lines = logf.readlines()
			logf.close()
			x = []
			selected_list = []
			dict = {}
			for i in range( len( lines )):
				if "Date : " in lines[i]:
					x.append ( i )
			for i in range(len(x)):
				if "Date : "+date1 in  lines[x[i]]:
					try:
						selected_list.append ( str(x[i])+"-"+str(x[i+1]) )
					except:	
						selected_list.append ( str(x[i])+"-"+str(len(lines)))
			for j in selected_list:
				for k in range( int(j.split('-')[0])+2 ,int( j.split('-')[1])) :
					if lines[k][0]=="_" :
						entry = lines[k].strip() 
						if lines[k][1]=="F":
							src = entry.split(',')[1] + "\\" + entry.split(',')[0][ entry.split(',')[0].rfind("\\")+1:]
							dest = entry.split(',')[0][4:entry.split(',')[0].rfind("\\")]
							dict[ entry.split(',')[1] ]= ""
							movef ( src ,dest, logO)
						else:
							srcd = entry.split(',')[1] 
							destd = entry.split(',')[0][4:entry.split(',')[0].rfind("\\")]+"\\"+ entry.split(',')[0][ entry.split(',')[0].rfind("\\")+1:]
							dict[ srcd[: srcd.rfind("\\")] ]= ""
							moved ( srcd ,destd ,logO) 
			delete_empty_folder ( dict ,logO )
			logO.close()
		else:
			print "Invalid syntaxx"
			print "python " + __file__  + " YYYY-MM-DD"
				
		
	else:
		print "Invalid syntax"
		print "python " + __file__  + " YYYY-MM-DD"