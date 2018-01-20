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
