(function(){
	console.log( "(function(){ ... })();");
	var  obj=document.elementFromPoint(_x,_y);
	var imgSrc = obj.getElementsByTagName("IMG");
	if ( imgSrc.length==0 )
		console.log("error in img object" , obj.Type);
		return;
	var photoId = imgSrc.src.split("/")[4]; 
	console.log( "PHOTOID : " , photoId);
	$.get("https://webapi.500px.com/photos/"+photoId,
	function(data, status){
        if( status != "success"){
          console.log("error in sending request" , status);
          return;
        }
        console.log("Status: " + status);
		var src = data.photo.image_url.pop();
        var link = document.createElement("a");
    	link.href = src;
    	link.download = null;
    	link.style.display = "none";
    	var evt = new MouseEvent("click", {
        "view": window,
        "bubbles": true,
        "cancelable": true
    	});

    	document.body.appendChild(link);
    	link.dispatchEvent(evt);
    	document.body.removeChild(link);
    	console.log("Downloaded...: "+ src);
	});
	return "_done";
})();