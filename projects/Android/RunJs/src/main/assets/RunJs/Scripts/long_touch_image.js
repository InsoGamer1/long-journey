(function(){
	var  obj=document.elementFromPoint(_x,_y);	
	var imgSrc = obj.parentNode.getElementsByTagName("IMG");
	if ( imgSrc.length==0 ){
		console.log("error in img object :" + obj.innerHTML);
		return;
	}
	var src = imgSrc[0].src;          
	var link = document.createElement("a"); 
	link.href = src; 
	link.download = null;  
	link.style.display = "none";  
	var evt = new MouseEvent("click",{"view": window,
		"bubbles": true,"cancelable": true });
	document.body.appendChild(link);
	link.dispatchEvent(evt);
	document.body.removeChild(link);
	console.log("Downloading... "+src)  
	return "_done";
})();