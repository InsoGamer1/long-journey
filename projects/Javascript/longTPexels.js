(function(){
	var obj=document.elementFromPoint(_x,_y);
	var imgSrc = obj.parentNode.getElementsByTagName("IMG");
	if ( imgSrc.length==0 ){
		console.log("error in img object" + obj.Type);
		return;
	}
	var photoid = imgSrc[0].src.match(/\d+/);
    if ( photoid != null ){
           var src = 'https://static.pexels.com/photos/'+photoid+'/pexels-photo-'+photoid+'.jpeg';
            var link = document.createElement("a"); 
			link.href = src;    
			link.download = src.split("/").pop();
			link.style.display = "none";
			var evt = new MouseEvent("click", 
			{"view": window,"bubbles": true,"cancelable": true}
			);
			document.body.appendChild(link);
			link.dispatchEvent(evt);
			document.body.removeChild(link);
			console.log("Downloading... "+src);
	}
	return "_done";
})();
