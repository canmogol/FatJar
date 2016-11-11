package fatjar.test;

import fatjar.HttpClient;
import fatjar.JSON;
import fatjar.Server;
import fatjar.internal.dto.HttpMethod;

public class WebTester {

    public static void main(String[] args) {
        WebTester tester = new WebTester();
        tester.serverStartTest();
    }

    public void serverStartTest() {
        // do a request to http://localhost:8080/ and/or http://localhost:8080/Hi
        Server.create()
                .listen(8080, "localhost")
                .get("/", (req, res) -> {
                    res.setContent("Welcome");
                    res.write();
                })
                .get("/Hi", (req, res) -> {
                    if (req.getParams().containsKey("name")) {
                        res.setContent("Hello " + req.getParams().getValue("name"));
                    } else {
                        res.setContent("type \"http://localhost:8080/Hi?name=john\" in your browser");
                    }
                    res.write();
                })
                .get("/httpClient", (req, res) -> {
                    try {
                        String content = HttpClient.create()
                                .url("http://ip.jsontest.com/")
                                .method(HttpMethod.GET)
                                .send()
                                .getContentAsString();
                        res.setContent("got content: " + content);
                    } catch (HttpClient.HttpClientException e) {
                        res.setContent("got exception: " + e);
                    }
                    res.write();
                })
                .get("/toJSON", (req, res) -> {
                    res.setContent(JSON.toJson(new MyPOJO("john", 101)));
                    res.write();
                })
                .post("/fromJSON", (req, res) -> {
                    MyPOJO myPOJO = JSON.fromJson(new String(req.getBody()), MyPOJO.class);
                    res.setContent(JSON.toJson(myPOJO));
                    res.write();
                })
                .post("/", (req, res) -> {
                })
                .delete("/", (req, res) -> {
                })
                .put("/", (req, res) -> {
                })
                .start();

    }

}
