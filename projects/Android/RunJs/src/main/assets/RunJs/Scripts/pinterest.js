/* Download an img */
function download(img) {
    var link = document.createElement("a");
    link.href = img.src;
    link.href=img.srcset.split(",").pop().trim().split(" ")[0];
    console.log( link.href);
    
    link.download = true;
    link.style.display = "none";
    
    var evt = new MouseEvent("click", {
        "view": window,
        "bubbles": true,
        "cancelable": true
    });

    document.body.appendChild(link);
    link.dispatchEvent(evt);
    document.body.removeChild(link);
    console.log("Downloading...");
    
}

/* Download all images in 'imgs'.
 * Optionaly filter them by extension (e.g. "jpg") and/or
 * download the 'limit' first only  */
function downloadAll(imgs, ext, limit) {
    /* If specified, filter images by extension */
    if (ext) {
        ext = "." + ext;
        imgs = [].slice.call(imgs).filter(function(img) {
            var src = img.src;
            return (src && (src.indexOf(ext, src.length - ext.length) !== -1));
        });
    }

    /* Determine the number of images to download */
    limit = (limit && (0 <= limit) && (limit <= imgs.length))
            ? limit : imgs.length;

    /* (Try to) download the images */
    for (var i = 0; i < limit; i++) {
        var img = imgs[i];
        //console.log(i+")IMG: " + img.src + " (", img, ")");
        download(img);
    }
}

/* Callback for button's "click" event */
function doit() {
    var imgs = document.querySelectorAll("img");
    if (imgs.length == 0 ){ // try other way
        imgs = document.getElementsByTagName("IMG");
        if (imgs.length == 0 ){
        alert ( "Nothing to download ");
        return ;
        }
    }
    downloadAll(imgs, "jpg", -1);
}

/* Create and add a "download" button on the top, left corner */
function addDownloadBtn() {
    var btn = document.createElement("button");
    btn.innerText = "Download all images";
    btn.addEventListener("click", doit);
    btn.style.position = "fixed";
    btn.style.top = btn.style.left = "0px";
    btn.style.zIndex = 1000;
    document.body.appendChild(btn);
}
addDownloadBtn();
//doit();