// enable Ajax
injectJs();
//$('#__traffic_man').remove();
var tm_html = ' <div id="__traffic_man" style="position:fixed;left:4%;top:4%; height:92%;width:88%;z-index:10000;border:2px solid green;border-radius:0%;">\
<div style="display:flex;position:relative;width:100%;height: 4%;background-color:green;color:white;text-align: center;">\
    <p style="display: inline-block;margin:auto;">TRAFFIC MANAGER</p>\
	<button style="float: right;" onclick="$(\'#__traffic_man\').toggle()">x</button>\
</div>\
<div id ="tm_result" style="position: relative;height:95%; width:100%; background-color: white;overflow:scroll;">RESULT</div>\
</div>';



if( $('#__traffic_man')[0] == undefined ){
         $(tm_html).appendTo( "body" );
         console.log( 'traffic manager added!!');  
}else{
          console.log( 'traffic manager already existing');
}
//return;

$.get("https://www.w3schools.com", 
  function(data, status){
            //alert("Data: " + data + "\nStatus: " + status);
  }
);


// http request and response handlers
$body = $("body"); 
$(document).on(
   { ajaxStart: 
      function(x,y) {

console.log('start');
console.log(x);
/*
for( v in x){
        console.log(v);
        console.log(JSON.stringify(x[v]));
}
*/
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
$.get("https://www.w3schools.com", 
  function(data, status){
            //alert("Data: " + data + "\nStatus: " + status);
  }
);





