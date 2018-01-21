(function(){
	var src = null;
	var obj=document.elementFromPoint(_x,_y);
	if ( obj == undefined ){
		console.log("No object found!! :" + obj.innerHTML);
	}
	var imgSrc = obj.parentNode.getElementsByTagName("IMG");
	if ( imgSrc.length >0 ){	
		var srcset = imgSrc[0].src;
		//console.log( "	 srcset:" + srcset);
		var src = srcset.split("/");
		if ( src.length >3 ){
			//console.log( "	 srcset:" + src[3]);
			src[3] = 'originals';
			//console.log( "	 srcset:" + src[3]);
			src =  src.join("/");
			console.log( "	 Src:" + src);
			var str ='No Image/Video found';
		}
	}
	if ( src ){
		var filterkeyImage = ".jpg" ;
		var filterkeyVideo = ".mp4" ;
		var filterkeyGif = ".gif" ;
		if ( src.indexOf(filterkeyImage) != -1 || src.indexOf(filterkeyVideo) != -1 || src.indexOf(filterkeyGif) != -1 ){
			console.log( "Starting download: "+src );
return;
			var link = document.createElement("a");
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
	}//eo src
})();
