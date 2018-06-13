var exec = require('cordova/exec');

exports.openPdf = function(url, header,options,success, error) {
    if (device.platform == "iOS") {
        exec(success || null, error || null, "DusalPdfViewer", "open", [url,'application/pdf']);
    } else {
        exec(success || null, error || null, "DusalPdfViewer", "openPdfUrl", [url,header,options]);
    }
};
