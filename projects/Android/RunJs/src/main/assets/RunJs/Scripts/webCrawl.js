console.log( "................inject JQuery.................");
var hrefList = [];
var tempHrefList = [];
var d=document;
var timeOutfn;
var waitingSecs = 0;
if(!d.getElementById('jquery')){
    var s=d.createElement('script');
    waitingSecs = 1000;
    s.src='https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js';
    s.id='jquery';
    d.body.appendChild(s);
    console.log( "all good things to those who waits" );
}

setTimeout(function(){
    console.log( "................ hrefs .................");
    var tagname = 'href';
    $("["+tagname+"]").each(function(i,x){ tempHrefList.push(x[tagname]); } );
    console.log( "................ srcs .................");
    var tagname = 'src';
    $("["+tagname+"]").each(function(i,x){ tempHrefList.push(x[tagname]); } );

    $.each(tempHrefList, function(i, el){
        if($.inArray(el, hrefList) === -1) hrefList.push(el);
    });
    console.log( hrefList );

}, waitingSecs); // wait for a sec


