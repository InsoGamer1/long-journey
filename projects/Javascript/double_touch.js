(function(){
	var lis= document.getElementsByTagName("IMG"); 
	console.log( lis.length);
	for( var i=0; i < lis.length ; i++){ 
		if ( lis[i].src.indexOf("photo")!=-1){
			src = lis[i].src;
			console.log( lis[i].src);
			var link = document.createElement("a");  
			link.href = src;   
			link.download = null;  
			link.style.display = "none";
			var evt = new MouseEvent("click", 
			 {"view": window, "bubbles": true,"cancelable":true}
			 );
			document.body.appendChild(link);
			link.dispatchEvent(evt);
			document.body.removeChild(link);
			console.log("Downloading... "+src);
			break;
		}
	}
	return "_done";
})();