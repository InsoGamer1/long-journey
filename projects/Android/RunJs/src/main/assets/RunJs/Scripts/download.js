/* Download an img */
function download(src) {
    var link = document.createElement("a");
    link.href = src.split("?")[0];
    link.download = src.split("?")[0].split("/").pop();
    link.style.display = "none";
    var evt = new MouseEvent("click", {
        "view": window,
        "bubbles": true,
        "cancelable": true
    });

    document.body.appendChild(link);
    link.dispatchEvent(evt);
    document.body.removeChild(link);
    console.log("Downloaded...: "+ src);
}