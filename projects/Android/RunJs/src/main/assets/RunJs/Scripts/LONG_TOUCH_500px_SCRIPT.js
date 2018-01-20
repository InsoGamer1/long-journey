(function(){
    var getImages = function ( obj ){
         var imgSrc = obj.getElementsByTagName("IMG");
	     if ( imgSrc.length==0 ){
             return null;
	     }
         return imgSrc;
    };
	var obj=document.elementFromPoint(_x,_y);
    var imgSrc;
    obj = obj.parentNode;
    imgSrc = getImages(obj);
    if ( !imgSrc ){
        obj = obj.parentNode.parentNode;
        imgSrc = getImages(obj);
        if ( !imgSrc ){
            console.log("error : " + obj.innerHTML);
            return;
        }
    }
	var imgSrc = obj.getElementsByTagName("IMG");
	if ( imgSrc.length==0 ){
        console.log("error : " + obj.innerHTML);
		return;
	}
	var src;
    var photoId = imgSrc[0].src.split("/")[4]; 
	$.get("https://webapi.500px.com/photos/"+photoId,
		function(data, status){
			if( status != "success"){
				console.log("error :" , status); 
				return;
			}        
			var src = data.photo.image_url.pop();
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
	);
	return "_done";
})();