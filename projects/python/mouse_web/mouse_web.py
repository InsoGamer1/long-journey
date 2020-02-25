#!/usr/bin/python
# mouse_web
# Author : Ahad Raheman

import pyautogui 

import SimpleHTTPServer
import SocketServer
from SimpleHTTPServer import SimpleHTTPRequestHandler
import json
from win10toast import ToastNotifier
toaster = ToastNotifier()



# class SimpleHTTPServer.SimpleHTTPRequestHandler(request, client_address, server)
class MouseController(SimpleHTTPRequestHandler):
	def _set_headers(self):
		self.send_response(200)
		self.send_header("Content-type", "text/html")
		self.end_headers()

	def _html(self, message):
		"""This just generates an HTML document that includes `message`
		in the body. Override, or re-write this do do more interesting stuff.
		"""
		content = "<html><body><h1>%s</h1></body></html>"%message
		return content.encode("utf8")  # NOTE: must return a bytes object!

	def do_GET(self):
		print self.path
		if self.path == '/' or self.path == '/youtube':
			self.path = '/index.html'
		return SimpleHTTPServer.SimpleHTTPRequestHandler.do_GET(self)
		# else:
			# self._set_headers()
			# self.wfile.write(self._html("hi!"))

	def do_HEAD(self):
		self._set_headers()
	
	def log_message(self, format, *args):
		pass
		# return SimpleHTTPRequestHandler.log_message( self , format , *args )

	def do_POST(self):
		try:
			self._set_headers()
			# print "in post method"
			self.data_string = self.rfile.read(int(self.headers['Content-Length']))
			self.send_response(200)
			self.end_headers()
			data = json.loads( self.data_string )
			print data
			if "type" in data:
				print data["type"]
				if data["type"] == "Lclick":
					pyautogui.click()
				elif data["type"] == "Rclick":
					pyautogui.rightClick()
				elif data["type"] == "Dclick":
					pyautogui.doubleClick()
				elif data["type"] == "key":
					key = data["string"]
					if "+" in key:#hotkeys
						pyautogui.hotkey(*key.split("+"))
					else:
						pyautogui.press(key)
				elif data["type"] == "move":
					data_int = { i:int(data["data"][i]) for i in data["data"] }
					x = data_int["x"]
					y = data_int["y"]
					print data_int , x , y
					pyautogui.moveRel(x, y, duration = 0.2) 
				elif data["type"] == "scroll":
					scroll = int(data["data"]["scroll"])
					print "scroll" ,  scroll
					pyautogui.scroll(scroll*10) 
				elif data["type"] == "hscroll":
					scroll = int(data["data"]["scroll"])
					print "hscroll" , scroll
					pyautogui.hscroll(scroll) 
				else:
					print "Invalid move!!"
			# Doesn't do anything with posted data
			self._set_headers()
			self.wfile.write(self._html("POST!"))
		except Exception,e:
			print e
			pass
		
def computeGCD(x, y): 
	while y: 
		x, y = y, x % y 
	return x

def getSize(width,height ):
	i=1
	gcd = computeGCD( width,height )
	width_ratio = width/gcd
	height_ratio = height/gcd
	x = max( width_ratio , height_ratio )
	while i*x < 100:
		i+=1
	mp = i-1
	return mp*width_ratio , mp*height_ratio

	
def main():
	print(pyautogui.size())
	pyautogui.FAILSAFE=False
	width,height = pyautogui.size()
	w,h = getSize( width,height)
	print w,h
	
	# pyautogui.moveTo(100, 100, duration = 1) 
	# pyautogui.moveTo(100, 100, duration = 1) 
	# print(pyautogui.position())
	# pyautogui.click(100, 100)
	# pyautogui.dragRel(100, 0, duration = 1)
	# pyautogui.scroll(200)
	# pyautogui.typewrite("hello Geeks !")
	# pyautogui.hotkey("ctrlleft", "a")
	
	PORT = 8000

	httpd = SocketServer.TCPServer(("", PORT), MouseController)

	print "serving at port", PORT
	toaster.show_toast("Sample Notification","MouseWeb started at %d!!!"%PORT)
	
	httpd.serve_forever()
	
if '__main__'==__name__:
	main()

