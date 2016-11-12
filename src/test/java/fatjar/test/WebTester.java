package fatjar.test;

import fatjar.*;
import fatjar.dto.HttpMethod;
import sample.MyEntity;

import java.util.List;

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
                .get("/db", (req, res) -> {

                    DB db = DB.create();

                    long numberOfAllEntities = db.count(MyEntity.class);
                    long numberOfJohnEntities = db.count(MyEntity.class, DB.Query.create("name", "five"));
                    long numberOfLargeIDEntities = db.count(MyEntity.class, DB.Query.create("id", DB.Sign.GT, 1L));
                    Log.info("numberOfLargeIDEntities: " + numberOfLargeIDEntities);

                    List<MyEntity> allEntities = db.findAll(MyEntity.class);
                    Log.info("found allEntities: " + allEntities);

                    MyEntity entityID1L = db.find(MyEntity.class, 1L);
                    Log.info("fount entityID1L: " + entityID1L);

                    MyEntity entity = new MyEntity("john");
                    MyEntity insertedEntity = db.insert(entity);
                    Log.info("insert insertedEntity: " + insertedEntity);

                    insertedEntity.setName("johnny");
                    db.update(insertedEntity);
                    Log.info("update insertedEntity: " + insertedEntity);

                    db.delete(insertedEntity);
                    Log.info("delete insertedEntity: " + insertedEntity);

                    res.setContent(JSON.toJson(insertedEntity));
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
