# FatJar
FatJar simple API to quick prototyping and portable web services. See **Main::main** test class for examples.

Build and coverage status:

[![Build Status](https://travis-ci.org/canmogol/FatJar.svg?branch=master)](https://travis-ci.org/canmogol/FatJar)

![codecov.io](https://codecov.io/github/canmogol/FatJar/branch.svg?branch=master)


Please see the fatjar.test.Main::main class for detailed examples,
also check the interfaces **HttpClient**, **JSON** and **Server** under **fatjar** package,
added a sample **MyEntity** class under fatjar.sample package as DB operations sample

Please find the swagger.yaml file as an example for service documentation

to create and start the server
```
Server.create().listen(80, "localhost").start();
```

Listen to an http method on a path
```
Server.create()
                .listen(80, "localhost")
                .get("/", (req, res) -> {
                    res.setContent("Welcome");
                    res.write();
                })
                .start();
```

To JSON and from JSON example
```
Server.create()
                .listen(80, "localhost")
                .get("/toJSON", (req, res) -> {
                    res.setContent(JSON.toJson(new MyPOJO("john", 101)));
                    res.write();
                })
                .post("/fromJSON", (req, res) -> {
                    MyPOJO myPOJO = JSON.fromJson(new String(req.getBody()), MyPOJO.class);
                    res.setContent(JSON.toJson(myPOJO));
                    res.write();
                })
                .start();
```

To make an Http request
```
Server.create()
                .listen(80, "localhost")
                .get("/httpClient", (req, res) -> {
                    try {
                        String content = HttpClient.create()
                                .url("http://ip.jsontest.com/")
                                .method(HttpMethod.GET)
                                .send()
                                .getContentAsString();
                        res.setContent("got content: " + content);
                    } catch (HttpClient.HttpClientException e) {
                        res.setContent("error: " + e);
                    }
                    res.write();
                })
                .start();
```



### Sample Test Request

After Main::main start

```
request :   GET   http://localhost:80/
response:   Welcome
```

```
request :   GET   http://localhost:80/Hi
response:   type "http://localhost:80/Hi?name=john" in your browser
```

```
request :   GET   http://localhost:80/Hi?name=john
response:   Hello john
```

```
request :   GET   http://localhost:80/toJSON
response:   {"age":101,"name":"john"}
```

Below example creates and writes to a file under tmp directory
```
request :   GET   http://localhost:80/file
response:   file content here!
```

```
request :   POST   http://localhost:80/fromJSON
            BODY   {"age":101,"name":"john"}
response:   {"age":101,"name":"john"}
```

A database method added, you may find the count, find, insert, update and delete methods under "/db" handler
```
request :   GET   http://localhost:80/db
response:   {"id":73,"name":"johnny"}
```

Example exception throw method
```
request :   GET   http://localhost:80/throwException
response:   {"error": "fatjar.Server$ServerException: tojsonexception", "request": {...}, "status": "500"}
```

Below example will make a request to a uri that is not handled 
```
request :   GET   http://localhost:80/notfound
response:   {"error":"fatjar.Server$ServerException","status":"500"}
```

/badRequest will return html content, 
this content is created by the Status.STATUS_BAD_REQUEST handler,
registered with the .register(Status.STATUS_BAD_REQUEST, ...) method
```
request :   GET   http://localhost:80/badRequest
response:   <h1>BAD REQUEST!</h1> 
```

```
request :   GET   http://localhost:80/httpClient
response:   got content: {"ip": "123.123.123.123"}
```
