var exec = require('cordova/exec');

exports.openPdf = function(url, header,options,success, error) {
    if (device.platform == "iOS") {
        window.open(encodeURI(url), '_blank', 'location=yes, EnableViewPortScale=yes');
    } else {
        exec(success, error, "DusalPdfViewer", "openPdfUrl", [url,header,options]);
    }
};
