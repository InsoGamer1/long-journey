// multi mode 
var highlightrowClass = ".highlightrow {background-color: #4285f4!important;color: white;}";
var selectedClass = 'highlightrow';
var seletedTrStack  = [];
var dragger = {
    start: function(e, ui) {
        $('.' + selectedClass).css({position:"absolute"});
    },
    drag: function(e, ui) {
        // this works because the position is relative to the starting position
            /*
        $('.' + selectedClass).css({
            top: ui.position.top,
            left: ui.position.left,
        });
        */
        $('.' + selectedClass).each(function(i,x){ 
            $(x).css({
            top: ui.position.top+(i*15),
            left: ui.position.left-(i*15),
            });
        });
    },
    stop: function(e, ui) {
        // reset group positions
        $('.' + selectedClass).css({
            top: 0,
            left: 0,
            position:"inherit",
        });
    },
};
multiSelectHandler = function( ev ) {
    if ( ev.shiftKey || ev.ctrlKey){
        var currentSelectedTrs = $("");
        if ( ev.shiftKey ){
            if ( seletedTrStack.length != 0 ){
                var lastfromStack = seletedTrStack[seletedTrStack.length-1];
                if ( lastfromStack[0].offsetTop < $(this)[0].offsetTop ){
                    currentSelectedTrs = $( seletedTrStack[seletedTrStack.length-1] ).nextUntil( $(this) , "tr" );
                }
                else{
                    currentSelectedTrs = $( seletedTrStack[seletedTrStack.length-1] ).prevUntil( $(this) , "tr" );
                }
                currentSelectedTrs.push($(this));
            }
        }
        else if ( ev.ctrlKey ){
            currentSelectedTrs = $(this);
        }
        currentSelectedTrs.each( function(i,x ){
            $(x).toggleClass(selectedClass);
            if ( $(x).hasClass(selectedClass)){
                $(x).draggable(dragger);
                seletedTrStack.push( $(x));
            }
            else{
                $(x).draggable("destroy");
                seletedTrStack.pop();
            }
        });
    }
    else{
        seletedTrStack  = [];
        $("."+selectedClass).draggable("destroy").removeClass( selectedClass );
    }
}
//var sourceQuery = "table tr"; 
//$(sourceQuery).click(multiSelectHandler);
//var targetQuery = "table tbody"; //shd be container
//$(targetQuery).droppable()
// END OF multi mode