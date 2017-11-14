scriptlist = document.getElementsByTagName("SCRIPT");
var hasjquery = 0 ;
for ( var i=0 ; i< scriptlist.length ; i++){
    if ( scriptlist[i].src.indexOf("jquery")!=-1){
        hasjquery = 1;
        break;
    }
}
if (!hasjquery){
    document.head.innerHTML += '<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>';
    console.log ( hasjquery);
}