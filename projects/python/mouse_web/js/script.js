let width_ratio=1;
let height_ratio=1;
function startup() {
  var el = document.getElementById("mouse_section");
  var lc = document.getElementById("left_click");
  var rc = document.getElementById("right_click");
  var ml = document.getElementById("middle_click");
  var ka = document.getElementById("key_area");
  var ss = document.getElementById("scroll_section");
  var hs = document.getElementById("hscroll_section");
  
  
  width_ratio = 1920/el.offsetWidth;
  height_ratio = 1080/el.offsetHeight;
  //console.log( "ratio , " , width_ratio , height_ratio);
  
  el.addEventListener("touchstart", handleStart, false);
  el.addEventListener("touchend", handleEnd, false);
  el.addEventListener("touchcancel", handleCancel, false);

  ss.addEventListener("touchstart", handleStart, false);
  ss.addEventListener("touchend", handleEnd, false);
  ss.addEventListener("touchcancel", handleCancel, false);

  hs.addEventListener("touchstart", handleStart, false);
  hs.addEventListener("touchend", handleEnd, false);
  hs.addEventListener("touchcancel", handleCancel, false);

  ka.addEventListener("click", keyHandler, false);
  lc.addEventListener("click", handleClick, false);
  rc.addEventListener("click", handleClick, false);
  ml.addEventListener("click", handleClick, false);
}

document.addEventListener("DOMContentLoaded", startup);
moveData = {};

function keyHandler(){
  let inputData = prompt('Type here');
  if ( inputData ){
  	moveData.type = "key";
    moveData.string = inputData;
    sendSignal();
  }
}

function handleStart(evt) {
  evt.preventDefault();
  console.log("touchstart." ,evt.changedTouches[0].screenX , evt.changedTouches[0].screenY);
//   console.log("cX: ",evt.changedTouches[0].clientX, "cY: ", evt.changedTouches[0].clientY , "sX: " , evt.changedTouches[0].screenX , "sY: ",evt.changedTouches[0].screenY,"pX: ",evt.changedTouches[0].pageX, "pY: ", evt.changedTouches[0].pageY);
  moveData.data = {};
  moveData.data.x1 = evt.changedTouches[0].screenX;
  moveData.data.y1 = evt.changedTouches[0].screenY;
}

function handleEnd(evt) {
  evt.preventDefault();
  console.log("handleEnd." ,evt.changedTouches[0].screenX , evt.changedTouches[0].screenY);
  moveData.type = "move";
  if ( evt.target.id == 'scroll_section'){
	  moveData.type = "scroll";
	  moveData.data.scroll = (evt.changedTouches[0].screenY - moveData.data.y1);
  }else if ( evt.target.id == 'hscroll_section'){
	  moveData.type = "hscroll";
	  moveData.data.scroll = (evt.changedTouches[0].screenX - moveData.data.x1);
  }else{
      moveData.data.x = (evt.changedTouches[0].screenX - moveData.data.x1)*width_ratio;
      moveData.data.y = (evt.changedTouches[0].screenY - moveData.data.y1)*height_ratio;
  }
  sendSignal();
}

function sendSignal(){
  console.log( "sending " , moveData);
  $.ajax({
			type: "POST",
			async: true,
			dataType: "json",
			url: "/",
			data:JSON.stringify(moveData),
	})
	.done(function (x) { moveData={}; } )
	.fail( function (x){ console.log(x);moveData={}; });	
}
function handleCancel(evt) {
  evt.preventDefault();
  console.log("handleCancel.");
  moveData = {}
}

function handleClick(evt) {
  evt.preventDefault();
  console.log(evt.target.id , "handleClick.");
  if ( evt.target.id=='left_click'){
    moveData.type = "Lclick";
    sendSignal();
  }else if ( evt.target.id=='right_click'){
    moveData.type = "Rclick";
    sendSignal();
  }else if ( evt.target.id=='middle_click'){
    moveData.type = "Dclick";
    sendSignal();
  }else{
    moveData = {};
  }
}





// function handleMove(evt) {
//   evt.preventDefault();
//   console.log("handleMove.");
// }

/*


function handleStart(evt) {
  evt.preventDefault();
  console.log("touchstart." ,evt.changedTouches[0].screenX , evt.changedTouches[0].screenY);
//   console.log("cX: ",evt.changedTouches[0].clientX, "cY: ", evt.changedTouches[0].clientY , "sX: " , evt.changedTouches[0].screenX , "sY: ",evt.changedTouches[0].screenY,"pX: ",evt.changedTouches[0].pageX, "pY: ", evt.changedTouches[0].pageY);
  moveData.type = "move";
  moveData.data = {};
  moveData.data.x = evt.changedTouches[0].screenX;
  moveData.data.y = evt.changedTouches[0].screenY;
  sendSignal();
}

function handleMove(evt) {
  evt.preventDefault();
  console.log("touchmove " ,evt.changedTouches[0].screenX , evt.changedTouches[0].screenY);
  moveData.type = "move";
  moveData.data = {};
  moveData.data.x = evt.changedTouches[0].screenX;
  moveData.data.y = evt.changedTouches[0].screenY;
  sendSignal();
}


function handleEnd(evt) {
  evt.preventDefault();
  console.log("handleEnd." ,evt.changedTouches[0].screenX , evt.changedTouches[0].screenY);
//   console.log("cX: ",evt.changedTouches[0].clientX, "cY: ", evt.changedTouches[0].clientY , "sX: " , evt.changedTouches[0].screenX , "sY: ",evt.changedTouches[0].screenY,"pX: ",evt.changedTouches[0].pageX, "pY: ", evt.changedTouches[0].pageY);
  moveData.type = "move";
  moveData.data = {};
  moveData.data.x = evt.changedTouches[0].screenX;
  moveData.data.y = evt.changedTouches[0].screenY;
  sendSignal();
}
*/