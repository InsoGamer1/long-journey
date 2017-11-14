function(){
	console.log( "(function(){ ... })();");
	_x = 388.21082; 
	_y = 784.4921; 
	console.log( "variable _x and _y has positionX and positionY" , _x , _y);
	var  obj=document.elementFromPoint(_x,_y);
	 if ( obj.parentNode ){
		   tag = 'video';
		   if ( obj.parentNode.getElementsByTagName(tag).length == 0 )
			   tag = 'img';
		   if ( srcNode = obj.parentNode.getElementsByTagName(tag)[0] )
			   var src = srcNode.src; 
		   else
			   var src = null;
	 }

	 else{
		   if ( srcNode = obj.getElementsByTagName(tag)[0] )
			   var src = srcNode.src; 
		   else
			   var src = null;
	 }
	 var str ='No Image/Video found';
	 if ( src ){
	   filterkeyImage = ".jpg" ;
	   filterkeyVideo = ".mp4" ;
	   if ( src.indexOf(filterkeyImage) != -1 || src.indexOf(filterkeyVideo) != -1){
			str = "Starting download: "+src ;        var link = document.createElement("a");
			link.href = src.split("?")[0];
			link.download = src.split("?")[0].split("/").pop();
			link.style.display = "none";
			var evt = new MouseEvent("click", {
				"view": window,
				"bubbles": true,
				"cancelable": true
			});
			document.body.appendChild(link);
			link.dispatchEvent(evt);
			document.body.removeChild(link);
	   }
	 }return str;})();