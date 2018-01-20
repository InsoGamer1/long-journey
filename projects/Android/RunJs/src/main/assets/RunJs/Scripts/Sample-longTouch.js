(function(){
	var  obj=document.elementFromPoint(_x,_y);
	if ( obj == undefined ){
		console.log("No object found!! :" + obj.innerHTML);
		return "_done";
	}
	console.log( " Object :");
	console.log( "\t Children Length:" + obj.parentNode.children.length);
    console.log( "\t tagName:" + obj.parentNode.tagName);
    console.log( "\t innerHTML:" + obj.parentNode.innerHTML );
	
	var imgSrc = obj.parentNode.getElementsByTagName("IMG");
	var vidSrc = obj.parentNode.getElementsByTagName("VIDEO");
	console.log( " Images :");
	console.log( "\t Length:" + imgSrc.length);
	
	console.log( " Videos :");
	console.log( "\t Length:" + vidSrc.length);
	
	return "_done";
})();
