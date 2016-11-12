# FatJar
FatJar simple API to quick prototyping and portable web services

please see the fatjar.test.WebTester class for detailed examples,
also check the interfaces **HttpClient**, **JSON** and **Server** under **fatjar** package,
added a sample **MyEntity** class under fatjar.sample package as DB operations sample

to create and start the server
```
Server.create().listen(8080, "localhost").start();
```

Listen to an http method on a path
```
Server.create()
                .listen(8080, "localhost")
                .get("/", (req, res) -> {
                    res.setContent("Welcome");
                    res.write();
                })
                .start();
```

To JSON and from JSON example
```
Server.create()
                .listen(8080, "localhost")
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
                .listen(8080, "localhost")
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

After WebTester start

```
request :   GET   http://localhost:8080/
response:   Welcome
```

```
request :   GET   http://localhost:8080/Hi
response:   type "http://localhost:8080/Hi?name=john" in your browser
```

```
request :   GET   http://localhost:8080/Hi?name=john
response:   Hello john
```

```
request :   GET   http://localhost:8080/toJSON
response:   {"age":101,"name":"john"}
```

```
request :   POST   http://localhost:8080/fromJSON
            BODY   {"age":101,"name":"john"}

response:   {"age":101,"name":"john"}
```

```
request :   GET   http://localhost:8080/httpClient
response:   got content: {"ip": "123.123.123.123"}
```

