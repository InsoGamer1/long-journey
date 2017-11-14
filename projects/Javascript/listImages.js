(function(){	 
	var lis= document.getElementsByTagName("IMG");
	console.log( lis.length);
	for( var i=0; i < lis.length ; i++){  
		if ( lis[i].src.indexOf("photo.jpg")!=-1)
			console.log( lis[i].src);
	}
	return "_done";
})();