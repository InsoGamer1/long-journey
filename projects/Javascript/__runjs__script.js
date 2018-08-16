


/* Author : Ahad Raheman */
/* scripts.js */
/* here lies the all the common functions  */ 
/* */

var download_dict = {};
function listFunctions(){
	console.log( " function listFunctions() " );
	console.log( " function addStyle( my_style_css_data ,id='__my__style' ) ");
	console.log( " function send(data,url,method='GET') " );
	console.log( " function dice(limit) " );
	console.log( " function download(src)  " );
	console.log( " function downloadAll(imgs, filterkey, limit)  " );
	console.log( " function injectJs() " );
	console.log( " function removeDuplicates(listimgs) " );	 
	console.log( " function listAll(tagname='IMG' , attr='src') " );
	console.log( " function injectScript(script_name , script_folder='file:///sdcard/RunJs/Scripts',waitingSecs = 1000;)" );
}

function addStyle( my_style_css_data ,id ){
  if ( id== undefined || id==null ){
    id = "__my__style";
  }

   /********** SAMPLE CSS DATA ******
  .blur {-webkit-filter: blur(4px);filter: blur(4px);}\
  .brightness {-webkit-filter: brightness(250%);filter: brightness(250%);}\
  .contrast {-webkit-filter: contrast(180%);filter: contrast(180%);}\
  .grayscale {-webkit-filter: grayscale(100%);filter: grayscale(100%);}
  .synced {  border: 4px solid green; }
  *****************/
     
   if( $('#'+id)[0] == undefined ){
       var my_style_css = '<style id="'+id+'" type="text/css" > '+my_style_css_data+'  </style>';
       $(my_style_css).appendTo( "head" );
       console.log( 'added style!!');
   }else{
        console.log( 'style already existing');
   }

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

function copyToClipboard(text) {
	var d = document;
	if (window.clipboardData && window.clipboardData.setData) {
		return clipboardData.setData("Text", text); 
	} else if (d.queryCommandSupported && d.queryCommandSupported("copy")){
		var textarea = document.createElement("textarea");
		textarea.textContent = text;
		textarea.style.position = "fixed";
		// Prevent scrolling to bottom of page in MS Edge. 
		d.body.appendChild(textarea); 
		textarea.select();
		try {
			return d.execCommand("copy"); 
		}catch (ex) { 
			console.warn("Copy to clipboard failed.", ex);
			return false;
	    }finally { 
			d.body.removeChild(textarea); 
		}
	}
}

function guessFileName( src ){
    console.log( download_dict[src] );
    if ( download_dict[src] == null || download_dict[src] == undefined )
        return  "" ;
    else
        return download_dict[src] ;
}

/* Download an img */
function download(src,prefix,name) {
    //alert( name   );
    var title = src.split("?")[0].split("/").pop();
    if ( prefix!=undefined)
        title = prefix + title ;
    if ( name!=undefined )
        title = name;
    //alert( title );
    var link = document.createElement("a");
    link.href = src;
    download_dict [ src ] = title ;
    link.download = title;
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
			console.log( ".......... hrefs .................");
			var tagname = 'href';
			$("["+tagname+"]").each(function(i,x){
				console.log( i,x[tagname])
			});
			console.log( "....... srcs .................");
			var tagname = 'src';
			$("["+tagname+"]").each(function(i,x){
				console.log( i,x[tagname]) 
			});
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


function injectScript(script_name , script_folder ,waitingSecs=1000){//1 sec waits after loads 
     if ( !script_folder)
           script_folder='file:///sdcard/RunJs/Scripts';
     if ( !waitingSecs)
           waitingSecs= 1000;
	var src= script_folder+'/'+ script_name +'.js';
	console.log( ".......injecting "+ src +" ...........");
	var d=document;
	var timeOutfn;
	var script_id = script_name+"_id";
	if(!d.getElementById(script_id)){
		var s=d.createElement('script');
		s.src= src;
		s.id=script_id;
		d.body.appendChild(s);
		console.log( "all good things to those who waits" );
		
		setTimeout(function(){
			console.log( src +" is now loaded!!" );
			return ;
		}, waitingSecs); // wait for a sec
	}
	else{
		console.log( src+" is already loaded" );
		return;
	}
}

function get500px_url( url ){
    if( url.indexOf("photo")!=-1){
        var photoId = url.split("/");
        if ( photoId.length < 5 )
             return null;
        photoId = photoId[4];
        var  url = "https://webapi.500px.com/photos/"+photoId;
        $.ajax({ url : url,
			type : "get", 
			async: false,
			success :  
			      function(data, status){
					if( status != "success"){ 
						console.log("error: " , status);
						return null;
					}else{
						//alert ( JSON.stringify ( data.photo.id ) );
						//return data.photo.image_url.pop();
						var dwn_link = null;
						dwn_link = data.photo.images.pop().https_url;
						//alert( dwn_link );
						download( dwn_link , null ,"HQ_500px_"+ data.photo.id+".jpg" );
					}
				},
			error: function() { connectionError(); } 
        });
     }
}

function gettumblr_url( url ){
  download( url );
}
function getinstagram_url( url ){
  try{
	download(  url.split("?")[0].split("/").pop() );
  }catch(err){
  	alert( "error: " + JSON.stringify( err ) );
  }
}


var colorClass ='blur';

function fetchall( url_parser, cssClass){
      //alert ( url_parser + cssClass );
      if ( !cssClass )
          cssClass = colorClass;
      $('.'+cssClass).each( function (i,x){
                //alert( x.outerHTML );
                url_parser( x.src );
                $(x).toggleClass( cssClass );
       });
}// End of fetchall

//console.log ( "__runjs__script.js is loaded " );
//alert ( "__runjs__script.js is loaded " );


/* 
// Uncomment this section ( remove / * and * / )
// to add your script after the page loads 
// and add your file and folder to below list injectingList

var injectingList = [
{"file":"hello" , "folder":'file:///sdcard/RunJs/Scripts'},
];
for( var i=0; i < injectingLists.length ; i++){  
	injectScript( injectingList[i].file , injectingList[i].folder );
}
*/


function sync_500px(query, cssClass){
      if ( !cssClass )
          cssClass = colorClass;
      $(query).addClass('synced').parent('a').unbind( "click" ).click( ev=> { 
             ev.preventDefault();
             $(ev.target).find('img').toggleClass( cssClass ); 
             //alert( $(ev.target).find('img'). length );
             //alert(ev.target.outerHTML);            
             return false;
      });
}


function sync_tumblr(query, cssClass){
      if ( !cssClass )
          cssClass = colorClass;
      $(query).addClass('synced').parent('a').unbind( "click" ).click( ev=> { 
             ev.preventDefault();
             $(ev.target).toggleClass( cssClass ); 
             //alert(ev.target.outerHTML);            
             return false;
      });
}


function _addButtons(query,   cssClass, sync_func , url_parser){
      if ( !cssClass )
          cssClass = colorClass;
      if ( !sync_func )
          sync_func = sync_500px;
      if ( !url_parser )
          url_parser = get500px_url;
   var my_style_css_data = '\
   .blur {-webkit-filter: blur(4px);filter: blur(4px);}\
   .brightness {-webkit-filter: brightness(250%);filter: brightness(250%);}\
   .contrast {-webkit-filter: contrast(180%);filter: contrast(180%);}\
   .grayscale {-webkit-filter: grayscale(100%);filter: grayscale(100%);}\
   .synced { border : 2px solid green !important;}\
   ';

   addStyle ( my_style_css_data, '__my__style'  );
   var download_btn ='<button id="__download__all"  style="position:fixed;left:89%;top:93%; height:50px;width:50px;z-index:1000;border:2px solid purple;border-radius:20%;">#</button>';

    if( $('#__download__all')[0] == undefined ){
         $(download_btn).appendTo( "body" ).click( 
         function (){
               fetchall( url_parser , cssClass);
          });
         console.log( 'added button!!');
    }else{
          console.log( 'button already existing');
    }
    
    var sync_btn ='<button id="__sync"  style="position:fixed;left:78%;top:93%; height:50px;width:50px;z-index:1000;border:2px solid purple;border-radius:20%;">S</button>';

   if( $('#__sync')[0] == undefined ){
         $(sync_btn).appendTo( "body" ).click( 
         	function(){ 
         		sync_func( query , cssClass );
         	}
         );
         console.log( 'sync added button!!');
    }else{
          console.log( 'sync button already existing');
    }
}

//********************** 500px *************************
if  ( document.URL.indexOf('500px.com')!=-1) {
	var _500_tag = 'a>img';
	_addButtons( _500_tag );
	sync_500px(_500_tag);
}
//******************************************************

//********************** tumblr **************************
if  ( document.URL.indexOf('tumblr.com')!=-1) {
     var tumblr_tag = 'a>img';
     _addButtons( tumblr_tag ,'grayscale', sync_tumblr ,gettumblr_url ); 
     sync_tumblr( tumblr_tag, 'grayscale' );
}
//*********************************************************

//********************** Instagram *************************
if  ( document.URL.indexOf('instagram.com')!=-1) {
     injectJs();
     setTimeout(function(){
			var insta_tag = 'a>img';
			_addButtons( insta_tag ,'grayscale', sync_tumblr ,getinstagram_url ); 
			sync_tumblr( insta_tag, 'grayscale' );
		}, 3000); // wait for a sec
     
    }
//***********************************************************

