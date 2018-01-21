(function(){
  var imgSrc = document.getElementsByTagName("IMG");
  for( xx=0 ;xx<imgSrc.length;xx++){
        var src = null;
		var srcset = imgSrc[xx].src;
		//console.log( "	 srcset:" + srcset);
		var src = srcset.split("/");
		if ( src.length >3 ){
			//console.log( "	 srcset:" + src[3]);
			src[3] = 'originals';
			//console.log( "	 srcset:" + src[3]);
			src =  src.join("/");
			//console.log( "	 Src:" + src);
		}
	if ( src ){
		var img = ".jpg" ;
		var vid = ".mp4" ;
		var gif = ".gif" ;
		if ( src.indexOf(img) != -1 || src.indexOf(vid) != -1 || src.indexOf(gif) != -1 ){
			console.log( "Startingg download: "+src );
            //return;
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
 }
})();
