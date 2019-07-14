/* Author : Ahad Raheman */
/* scripts.js */
/* here lies the all the common functions  */ 
/* */

function listFunctions(){
	console.log( " function listFunctions() " );
	console.log( " function send(data,url,method='GET') " );
	console.log( " function dice(limit) " );
	console.log( " function download(src)  " );
	console.log( " function downloadAll(imgs, filterkey, limit)  " );
	console.log( " function injectJs() " );
	console.log( " function removeDuplicates(listimgs) " );	 
	console.log( " function listAll(tagname='IMG' , attr='src') " );
	console.log( " function injectScript(script_name) " );
}
	
function send(data,url,method='GET'){
	$.ajax({
			type: method,
			async: true,
			dataType: "json",
			url: url,
			data:JSON.stringify(data),
	})
	.done(function (x) { return JSON.stringify(x); } )
	.fail( function (x){ console.log(x);return null;});
}


function dice(limit){
	console.log  (Math.floor( (Math.random() *10) %limit));
}


/* Download an img */
function download(src) {
    var link = document.createElement("a");
    link.href = src;
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
	console.log("Downloaded...: "+ src);
}

/* Download all images in 'imgs'.
 * Optionaly filter them by extension (e.g. "jpg") and/or
 * download the 'limit' first only  */
function downloadAll(imgs, filterkey, limit) {
    /* If specified, filter images by extension */
    if (filterkey) {
        imgs = [].slice.call(imgs).filter(function(img) {
            var src = img.src;
            return (src && (src.indexOf(filterkey) !== -1));
        });
    }

    /* Determine the number of images to download */
    limit = (limit && (0 <= limit) && (limit <= imgs.length))
            ? limit : imgs.length;

    /* (Try to) download the images */
    for (var i = 0; i < limit; i++) {
        var img = imgs[i];
        console.log(i+")IMG: " + img.src );
        download(img.src);
    }
	return;
}

function injectJs(){
	console.log( "................inject JQuery.................");
	var d=document;
	var timeOutfn;
	var waitingSecs = 0;
	if(!d.getElementById('jquery')){
		var s=d.createElement('script');
		waitingSecs = 3000;
		s.src='//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js';
		s.id='jquery';
		d.body.appendChild(s);
		console.log( "all good things to those who waits" );
		
		setTimeout(function(){
			console.log( "JQuery is now loaded!!" );
			return ;
			console.log( "................ hrefs .................");
			var tagname = 'href';
			$("["+tagname+"]").each(function(i,x){ console.log( i,x[tagname]) } );
			console.log( "................ srcs .................");
			var tagname = 'src';
			$("["+tagname+"]").each(function(i,x){ console.log( i,x[tagname]) } );
		}, waitingSecs); // wait for a sec
	}
	else{
		console.log( "JQuery is already loaded" );
		return;
	}
}

function removeDuplicates(listimgs){	 
	var uniqueNames = []; 
	$.each(listimgs, function(i, el){
		if($.inArray(el, uniqueNames) === -1) 
			uniqueNames.push(el);
	});
	return uniqueNames;
}
function listAll(tagname="IMG" , attr="src"){
	var lis= document.getElementsByTagName(tagname);
	console.log( lis.length);
	for( var i=0; i < lis.length ; i++){  
		console.log( lis[i].attr);
	}
}


function injectScript(script_name){
	console.log( "................injecting "+ script_name +" .................");
	var d=document;
	var timeOutfn;
	var waitingSecs = 0;
	var script_id = script_name+"_id";
	if(!d.getElementById(script_id)){
		var s=d.createElement('script');
		waitingSecs = 1000;
		s.src='//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js';
		s.id=script_id;
		d.body.appendChild(s);
		console.log( "all good things to those who waits" );
		
		setTimeout(function(){
			console.log( "JQuery is now loaded!!" );
			return ;
		}, waitingSecs); // wait for a sec
	}
	else{
		console.log( script_name+" is already loaded" );
		return;
	}
}


