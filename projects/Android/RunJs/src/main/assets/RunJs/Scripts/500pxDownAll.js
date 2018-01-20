(function(){
	console.log( "(function(){ ... })();");
	var processedImgs={};
	$("IMG").each( function (i,x){
		if( x.src.indexOf("photo")!=-1){
			var photoId = x.src.split("/")[4];
			var src;
			$.get("https://webapi.500px.com/photos/"+photoId,
				function(data, status){
					if( status != "success"{          
						console.log("error: " , status);          
						return;
					}
					src = data.photo.image_url.pop();
					if ( !processedImgs[src] {
						console.log( "PHOTOID : "  +photoId);
						processedImgs[src]=1;
						var link =document.createElement("a");    
						link.href = src;
						link.download = null;    
						link.style.display = "none";
						var evt = new MouseEvent("click",{
							"view": window,
							"bubbles": true,
							"cancelable": true
						});
						document.body.appendChild(link);
						link.dispatchEvent(evt);
						document.body.removeChild(link);
						console.log("Downloading... "+src);
					}
				}
			);
		}
	});
	return "_done";
})();