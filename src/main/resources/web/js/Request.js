// ajax request
Request = {

    send: function (handler/*handler object with example signature below*/) {
        /*
         {
         url: "http://ip.jsontest.com",
         method: "GET",
         cancelled: false,
         async: true,
         headers: {"x-http-requester":"X212"},
         data: {"username":"asd", "password":"123"},
         error : function(e){console.log("error: "+e)},
         requestNotInitialized : function(){console.log("requestNotInitialized")},
         serverConnectionEstablished : function(){console.log("serverConnectionEstablished")},
         requestReceived : function(){console.log("requestReceived")},
         processingRequest : function(){console.log("processingRequest")},
         requestFinishedResponseReady : function(req){console.log("CALL CALLBACK! requestFinishedResponseReady, req: "+JSON.stringify(req))}
         }
         */

        /*
         onreadystatechange
         Stores a function (or the name of a function) to be called automatically each time the readyState property changes

         readyState
         Holds the status of the XMLHttpRequest. Changes from 0 to 4:
         0: request not initialized
         1: server connection established
         2: request received
         3: processing request
         4: request finished and response is ready

         status
         200: "OK"
         404: Page not found
         ...
         */
        var req = createXMLHTTPObject();
        if (!req) {
            handler.error("could not create request object for this browser");
        }
        // DOMString method, DOMString url, optional boolean async, optional DOMString? user, optional DOMString? password
        req.open(handler.method, handler.url, handler.async);

        // user added headers
        for (var key in handler.headers) {
            try {
                var value = handler.headers[key];
                if (!(value instanceof Object)) {
                    req.setRequestHeader(key, handler.headers[key]);
                }
            } catch (e) {
            }
        }
        //
        if (handler.method == "POST") {
            req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        }

        // for each state change, this method will be called
        req.onreadystatechange = function () {
            if (handler.cancelled == true) {
                try {
                    if (req.abort != null) {
                        req.abort();
                    }
                } catch (e) {
                    console.debug("exception while aborting the request, e: " + e);
                }
                if (!handler.onCancelled) {
                    handler.onCancelled = true;
                    handler.onCancel();
                }
            } else {
                if (req.readyState == 0) {
                    handler.requestNotInitialized(); //0	UNSENT	open() has not been called yet.
                } else if (req.readyState == 1) {
                    handler.serverConnectionEstablished(); //1	OPENED	send() has not been called yet.
                } else if (req.readyState == 2) {
                    handler.requestReceived(); //2	HEADERS_RECEIVED	send() has been called, and headers and status are available.
                } else if (req.readyState == 3) {
                    handler.processingRequest(); //3	LOADING	Downloading; responseText holds partial data.
                } else if (req.readyState == 4) {
                    // 4	DONE	The operation is complete.
                    if (req != null && req != undefined) {
                        var response = null;
                        if (req.response != null && req.response != undefined) {
                            response = req.response;
                        } else if (req.responseText != null && req.responseText != undefined) {
                            response = req.responseText;
                        } else {
                            handler.error("response and responseText empty!");
                        }
                        if (response != null) {
                            try {
                                if (!(req.response instanceof Object)) {
                                    response = JSON.parse(response);
                                }
                            } catch (e) {
                                console.log("exception occurred while returning response, e: " + e);
                            }
                            handler.requestFinishedResponseReady(req, response);
                        }
                    } else {
                        handler.error("req is not defined!");
                    }
                }
            }
        };
        // request is finished already and response is ready, so do not call send() method again
        if (req.readyState != 4) {
            try {
                req.send(handler.data);
            } catch (e) {
                handler.error(e);
            }
        }

        function createXMLHTTPObject() {
            var XMLHttpFactories = [
                function () {
                    return new XMLHttpRequest()
                },
                function () {
                    return new ActiveXObject("Msxml2.XMLHTTP")
                },
                function () {
                    return new ActiveXObject("Msxml3.XMLHTTP")
                },
                function () {
                    return new ActiveXObject("Microsoft.XMLHTTP")
                }
            ];
            var req = false;
            for (var i = 0; i < XMLHttpFactories.length; i++) {
                try {
                    req = XMLHttpFactories[i]();
                } catch (e) {
                    continue;
                }
                break;
            }
            return req;
        }
    }

};