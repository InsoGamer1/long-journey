function(){	  //code goes here
 imgs = document.getElementsByTagName("IMG");
 for ( var i =0 ; i<imgs. length; i++){
  if ( imgs[i].src.indexOf("_n.jpg")!=-1)
     console.log(imgs[i].src);
 }
}