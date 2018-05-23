# cordova-plugin-dusal-pdfviewer


### Installation cordova plugin

```
cordova plugin add https://github.com/almas/cordova-plugin-dusal-pdfviewer.git
```

### Supported Platforms
- Android
- iOS

### Example

```
var options = {
    headerColor:"#000000",
    showScroll:true,
    showShareButton:true,
    showCloseButton:true,
    swipeHorizontal:false
  };

DusalPdfViewer.openPdf(url, title, options,
    function(success){
        // success callback
    },function(error){
        // error callback
    });
```
