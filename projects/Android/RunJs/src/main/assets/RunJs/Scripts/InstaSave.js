// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// Send back to the popup a sorted deduped list of valid link URLs on this page.
// The popup injects this script into all frames in the active tab.
function download(src,key) {
    var link = document.createElement("a");
    link.href = src;
    link.download = key;
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


var links = [].slice.apply(document.getElementsByTagName('img'));

links = links.map(function(element) {
  return element.src;
});

links.sort();

for (var i = 0; i < links.length;) {
  if (((i > 0) && (links[i] == links[i - 1])) || (links[i] == '') ) {
    links.splice(i, 1);
  }else {
    ++i
  }
}



instaPrefix = "?ig_cache_key=";
for (var i = 0; i < links.length || i< 50;i++) {
  instaIndex = links[i].indexOf(instaPrefix);
  if ( instaIndex!=-1 ){
    //console.log( links[i].substring( links[i].lastIndexOf("/")+1 , instaIndex ));
    console.log( "attempt to download ");
    download( links[i] , links[i].substring( links[i].lastIndexOf("/")+1 , instaIndex ) );
  }
}



//console.log( "attempt to download "  + links[0]);
//download( links[0] , );