(function(){
	console.log( "(function(){ ... })();");
	var photoId = $("IMG")[0].src.split("/")[4];
	console.log( "PHOTOID : " , photoId);
	var src;
	$.get("https://webapi.500px.com/photos/"+photoId, function(data, status){
        if( status != "success"){
          console.log("error in sending request" , status);
          return;
        }
        console.log("Status: " + status);
		src = data.photo.image_url.pop();
        console.log( "SRC : " , src);
	});
	return src;
})();