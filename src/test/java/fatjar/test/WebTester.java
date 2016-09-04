package fatjar.test;

import fatjar.server.Server;

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
                .post("/", (req, res) -> {
                })
                .delete("/", (req, res) -> {
                })
                .put("/", (req, res) -> {
                })
                .start();

    }

}
