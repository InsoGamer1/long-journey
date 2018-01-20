(function(){
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
}
setTimeout(function(){
    console.log( "................ hrefs .................");
    var tagname = 'href';
    $("["+tagname+"]").each(
		function(i,x){ 
		console.log( i,x[tagname]);
		}
	);
	console.log( "................ srcs .................");
	var tagname = 'src';
	$("["+tagname+"]").each(
		function(i,x){
			console.log( i,x[tagname]);
		}
	);
}, waitingSecs); // wait for a sec
return "_done";
})();