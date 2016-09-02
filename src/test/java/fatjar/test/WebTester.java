package fatjar.test;

import fatjar.server.undertow.UndertowServer;

public class WebTester {

    public static void main(String[] args) {
        WebTester tester = new WebTester();
        tester.serverStartTest();
    }

    public void serverStartTest() {
        UndertowServer.create()
                .listen(8080, "localhost")
                .get("/", (req, res) -> {
                    res.setContent("Welcome");
                    res.write();
                })
                .get("/Hi", (req, res) -> {
                    res.setContent("Hi from get");
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
