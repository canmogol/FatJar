package sample;

import fatjar.*;
import fatjar.dto.HttpMethod;
import fatjar.dto.Status;

import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        Main tester = new Main();
        tester.exampleServer();
    }

    private void exampleServer() {
        // do a request to http://localhost:80/ and/or http://localhost:80/Hi
        Server.create().listen(8080, "localhost").register(Status.STATUS_BAD_REQUEST, (req, res) -> {
            res.setContentType("text/html");
            res.setContent("<h1>BAD REQUEST!</h1>");
            res.write();
        }).filter("/*", (req, res) -> {
            Log.info("Wildcard filter called");
        }).filter("/Hi", (req, res) -> {
            Log.info("/Hi filter called");
        }).get("/", (req, res) -> {
            res.setContent("Welcome");
            res.write();
        }).get("/Hi", (req, res) -> {
            if (req.getParams().containsKey("name")) {
                res.setContent("Hello " + req.getParams().getValue("name"));
            } else {
                res.setContent("type \"http://localhost:80/Hi?name=john\" in your browser");
            }
            res.write();
        }).get("/cache", (req, res) -> {
            Cache<String, Object> cache = Cache.create("FatJarLocalCache");
            cache.put("key", "value");
            if (cache.get("number") == null) {
                cache.put("number", 1);
            } else {
                cache.put("number", (Integer) cache.get("number") + 1);
            }
            res.setContent(JSON.toJson(cache.getAll()));
            res.write();
        }).get("/file", (request, response) -> {
            String path = "tmp";
            String fileName = "file.temp";
            String fileContent = "file content here!";
            boolean result = IO.writeFile(path, fileName, fileContent);
            if (result) {
                Optional<String> content = IO.readFile(path, fileName);
                if (content.isPresent()) {
                    response.setContent(content.get());
                    response.write();
                } else {
                    throw new Server.ServerException(Status.STATUS_NOT_FOUND);
                }
            } else {
                throw new Server.ServerException(Status.STATUS_INTERNAL_SERVER_ERROR, "could not create file");
            }
        }).get("/db", (req, res) -> {

            Optional<DB> dbOptional = DB.create();
            if (dbOptional.isPresent()) {
                DB db = dbOptional.get();
                long numberOfAllEntities = db.count(MyEntity.class);
                long numberOfJohnEntities = db.count(MyEntity.class, DB.Query.create("name", "five"));
                long numberOfLargeIDEntities = db.count(MyEntity.class, DB.Query.create("id", DB.Sign.GT, 1L));
                Log.info("numberOfLargeIDEntities: " + numberOfLargeIDEntities);

                List<MyEntity> allEntities = db.findAll(MyEntity.class);
                Log.info("found allEntities: " + allEntities);

                MyEntity entityID1L = db.find(MyEntity.class, 1L);
                Log.info("fount entityID1L: " + entityID1L);

                MyEntity entity = new MyEntity("john");
                Optional<MyEntity> insertedEntityOptional = db.insert(entity);
                if (insertedEntityOptional.isPresent()) {
                    MyEntity insertedEntity = insertedEntityOptional.get();
                    Log.info("insert insertedEntity: " + insertedEntity);

                    insertedEntity.setName("johnny");
                    db.update(insertedEntity);
                    Log.info("update insertedEntity: " + insertedEntity);

                    db.delete(insertedEntity);
                    Log.info("delete insertedEntity: " + insertedEntity);
                    res.setContent(JSON.toJson(insertedEntity));
                } else {
                    res.setContent("could not insert to DB");
                }
                res.write();
            } else {
                throw new Server.ServerException("no db available");
            }
        }).get("/httpClient", (req, res) -> {
            try {
                String content = HttpClient.create().url("http://ip.jsontest.com/").method(HttpMethod.GET).send().getContentAsString();
                res.setContent("got content: " + content);
            } catch (HttpClient.HttpClientException e) {
                res.setContent("got exception: " + e);
            }
            res.write();
        }).get("/throwException", (req, res) -> {
            throw new Server.ServerException("tojsonexception");
        }).get("/badRequest", (req, res) -> {
            throw new Server.ServerException(Status.STATUS_BAD_REQUEST);
        }).get("/toJSON", (req, res) -> {
            res.setContent(JSON.toJson(new MyPOJO(req.getParam("name", "add a query parameter like '?name=john'"), 101)));
            res.write();
        }).post("/fromJSON", (req, res) -> {
            MyPOJO myPOJO = JSON.fromJson(new String(req.getBody()), MyPOJO.class);
            res.setContent(JSON.toJson(myPOJO));
            res.write();
        }).post("/", (req, res) -> {
        }).delete("/", (req, res) -> {
        }).put("/", (req, res) -> {
        }).start();
    }
}
