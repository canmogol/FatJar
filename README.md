# FatJar
FatJar simple API to quick prototyping and portable web services

please see the fatjar.test.WebTester class for detailed examples

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
                                .url("http://localhost:8080/toJSON")
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

