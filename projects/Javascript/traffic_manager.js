// enable Ajax
injectJs();
//$('#__traffic_man').remove();

(function(){
console.log("................injecting JQuery.................");
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
}
})();




var tm_html = ' <div id="__traffic_man" style="position:fixed;left:4%;top:4%; height:92%;width:88%;z-index:10000;border:2px solid green;border-radius:0%;">\
<div style="display:table;position:relative;width:100%;height: 4%;background-color:green;color:white;text-align: center;">\
    <p style="display: inline-block;margin:auto;">TRAFFIC MANAGER</p>\
	<button style="float: right;" onclick="$(\'#__traffic_man\').toggle()">x</button>\
</div>\
<div id ="tm_result" style="position: relative;height:96%; width:100%; background-color: white;overflow:auto;"></div>\
</div>';



if( $('#__traffic_man')[0] == undefined ){
         $(tm_html).appendTo( "body" );
         console.log( 'traffic manager added!!');  
}else{
          console.log( 'traffic manager already existing');
}
//return;


// http request and response handlers

XMLHttpRequest.prototype.realSend = XMLHttpRequest.prototype.send;
XMLHttpRequest.prototype.send = function(value) {
    this.addEventListener("progress", function(){
        console.log("progress" , this.responseURL );
        var id = this.responseURL.replace(/[\/\.\:\?\=]/g, "_");
        if ( $('#'+id)[0] == undefined)
          $('#tm_result').append( '<br><div id="'+id+'" style="border: #6a6a6c 2px solid;" ><pre>Request : <a style="color: blue;font-style: italic;" onclick="$(this).siblings(\'div\').toggle();">' +this.responseURL +'<a></pre></div>');
        //else
          //$('#'+id+">pre").append( "Request : " + this.responseURL );
    }, false);

    this.addEventListener("load", function(){
        console.log("load" , this.responseURL);
        var id = this.responseURL.replace(/[\/\.\:\?\=]/g, "_");
        var len = $('#'+id+">pre pre").length+1;
        $('#'+id+">pre")[0].innerHTML += '<div style="border: 1px none #a8a3a3;"><span style="background-color: #e5dfdf;" onclick="$(this).next(\'pre\').toggle();">'+len+'.Response :</span><pre style="display:none;background-color: #f5ffe6;">'+this.responseText+"</pre></div>";
    }, false);
    this.realSend(value);
};


/*
$body = $("body"); 
$(document).on(
   { ajaxStart: 
      function(x,y) {

console.log('start');
console.log(this);

for( v in x){
        console.log(v);
        console.log(JSON.stringify(x[v]));
}

logx( x.type );
logx( x.target.location );
$('#tm_result').append( '<div id="'+x.target.id+'" ><pre>'+JSON.stringify( x.target )+'</pre></div>');

//logx(y);
        //$body.addClass("loading");  
      }
     , ajaxStop:
      function(x,y) { 
console.log('stop');

logx( x.type );
logx( x.target );
         //$body.removeClass("loading"); 
      } 
});
*/
/*
$.get("https://jsonplaceholder.typicode.com/todos/1" , function( data , status ) { console.log( data ) } );
*/




