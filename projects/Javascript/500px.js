var photoId = $("IMG")[0].src.split("/")[4];
$.get("https://webapi.500px.com/photos/"+photoId, function(data, status){
        if( status != "success"){
          console.log("error in sending request" , status);
          return;
        }
        console.log("Status: " + status);
        console.log( data.photo.image_url.pop());
});
