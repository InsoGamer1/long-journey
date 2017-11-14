( function (){
  download = function (src){	 
	    var link = document.createElement("a");  
	    link.href = src;   
	    link.download = null;  
	    link.style.display = "none";
	    var evt = new MouseEvent("click", 
	    {"view": window,"bubbles": true,"cancelable": true}
	    );    
	    document.body.appendChild(link);
	    link.dispatchEvent(evt);
	    document.body.removeChild(link);
	    console.log("Downloading... "+src);
  };
  return "_done";
})();