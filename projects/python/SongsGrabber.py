import os,urllib2
from bs4 import BeautifulSoup
import requests



if __name__ == "__main__":
	tarDir = r'C:\Users\Raheman\Desktop\TSFH'
	url1 = r'http://web.ican.ie/ican.mmm/English/TSFH%20Discography/2006%20-%20Two%20Steps%20From%20Hell/cd1/'
	url2 = r'http://web.ican.ie/ican.mmm/English/TSFH%20Discography/2006%20-%20Two%20Steps%20From%20Hell/cd2/cd2/'
	url3 = r'http://web.ican.ie/ican.mmm/English/TSFH%20Discography/2006%20-%20Two%20Steps%20From%20Hell/cd3/'
	urlList = [ url1 , url2 , url3]
	urlList = [ url3]
	for url in urlList:
		print urllib2.urlparse.unquote(url)
		req = requests.get(url)
		data = req.text
		soup = BeautifulSoup(data , "html.parser")
		for link in soup.find_all('a'):
			songUrl = url + link.get('href')
			filename = urllib2.urlparse.unquote(link.get('href'))
			if filename.endswith(".mp3") and 'orc' not in filename:
				# downloading 
				print "Downloading....." , filename
				if not os.path.exists( tarDir+"\\"+filename):
					songReq = urllib2.urlopen(songUrl)
					songContent = songReq.read()
					songFd = open ( tarDir+"\\"+filename , "wb")
					songFd.write ( songContent)
					songFd.close()
				print "Done!!!!!!!!!!!!!"
				
