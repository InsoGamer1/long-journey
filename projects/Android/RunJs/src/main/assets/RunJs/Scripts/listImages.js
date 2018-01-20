(function(){	 
	var lis= document.getElementsByTagName("IMG");
	console.log( lis.length);
	for( var i=0; i < lis.length ; i++){  
		console.log( lis[i].src);
	}
	return "_done";
})();

