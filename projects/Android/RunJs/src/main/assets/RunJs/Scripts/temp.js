(function(){
	var  obj=document.elementFromPoint(_x,_y);
	if ( obj == undefined ){
		console.log("No object found!! :" + obj.innerHTML);
	}
	var imgSrc = obj.parentNode.getElementsByTagName("IMG");
    var srcset = imgSrc[0].srcset.split(",");
    var imgsrc = srcset[srcset.length-1].split(" ")[0];
    console.log( "\t srcset:" + imgsrc);

})();
