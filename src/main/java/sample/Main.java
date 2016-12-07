package sample;

import fatjar.*;
import fatjar.dto.HttpMethod;
import fatjar.dto.Param;
import fatjar.dto.RequestKeys;
import fatjar.dto.Status;

import java.io.File;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;


public class Main {

    public static void main(String[] args) {
	Main tester = new Main();
	if (args.length == 1) {
	    tester.scriptServer(args[0]);
	} else {
	    tester.exampleServer();
	}
    }

    private void scriptServer(String scriptName) {
	Script.Type type = Script.Type.from(scriptName.substring(scriptName.lastIndexOf(".") + 1));
	Script.create(type).evaluate(IO.readFile(scriptName).get());
    }

    private int getAssignedPort() {
	ProcessBuilder processBuilder = new ProcessBuilder();
	Log.info("PORT environment value: " + processBuilder.environment().get("PORT"));
	if (processBuilder.environment().get("PORT") != null) {
	    return Integer.parseInt(processBuilder.environment().get("PORT"));
	}
	return 8080;
    }

    private void exampleServer() {
	// do a request to http://localhost:80/ and/or http://localhost:80/Hi
	Server.create(
		new HashMap<Server.ServerParams, String>() {{
		    put(Server.ServerParams.SIGN_KEY, "1234567812345678");
		    put(Server.ServerParams.APPLICATION_NAME, "FAT_JAR_EXAMPLE_APP");
		}})
		.listen(this.getAssignedPort(), "0.0.0.0")
		.register(Status.STATUS_BAD_REQUEST, (req, res) -> {
		    res.setContentType("text/html");
		    res.setContent("<h1>BAD REQUEST!</h1>");
		    res.write();
		})
		.filter("/*", (req, res) -> {
		    Log.info("Wildcard filter called");
		})
		.filter("/aa/*", (req, res) -> {
		    String uri = req.getHeader(RequestKeys.URI);
		    if (req.getSession() == null || req.getSession().get("username") == null) {
			throw new Server.ServerException(Status.STATUS_UNAUTHORIZED, "unauthorized call to " + uri);
		    }
		})
		.filter("/Hi", (req, res) -> {
		    Log.info("/Hi filter called");
		})
		.get("/", (req, res) -> {
		    res.setStatus(Status.STATUS_SEE_OTHER);
		    res.getHeaders().addParam(new Param<>("Location", "/html/index.html"));
		    res.write();
		})
		.get("/Hi", (req, res) -> {
		    if (req.getQueryParams().containsKey("name")) {
			res.setContent("Hello " + req.getQueryParams().getValue("name"));
		    } else {
			res.setContent("type \"http://localhost:80/Hi?name=john\" in your browser");
		    }
		    res.write();
		})
		.get("/@folder/@file", (req, res) -> {
		    String uri = req.getHeader(RequestKeys.URI);
		    res.setContentType(req.getMimeType(uri));
		    Optional<byte[]> content = IO.readBinaryResource(req.getQueryParam("@folder"), req.getQueryParam("@file"));
		    if (content.isPresent()) {
			res.setContentChar(content.get());
		    } else {
			throw new Server.ServerException(Status.STATUS_NOT_FOUND, "no file found with uri: " + uri);
		    }
		    res.write();
		})
		.post("/login", (req, res) -> {
		    if (!req.hasPostParams("username", "password")) {
			IO.readResource("template", "freemarker", "error.ftl")
				.ifPresent(content -> {
				    res.setContentType("text/html");
				    res.setContent(
					    Template.create().fromTemplate(
						    content,
						    "error", "login username and/or password fields are empty "
					    )
				    );
				});
		    } else if (!"john".equals(req.getPostParam("username")) || !"123".equals(req.getPostParam("password"))) {
			IO.readResource("template", "freemarker", "error.ftl")
				.ifPresent(content -> {
				    res.setContentType("text/html");
				    res.setContent(
					    Template.create().fromTemplate(
						    content,
						    "error", "username and/or password wrong"
					    )
				    );
				});
		    } else {
			req.getSession().put("lastLogin", new Date().toString());
			req.getSession().putEncrypt("username", req.getPostParam("username"));

			res.setStatus(Status.STATUS_SEE_OTHER);
			res.getHeaders().addParam(new Param<>("Location", "/aa/logged"));
			Cache.create().put("username", req.getPostParam("username"));
		    }
		    res.write();
		})
		.get("/aa/logged", (req, res) -> {
		    IO.readResource("template", "freemarker", "login.ftl")
			    .ifPresent(content -> {
				res.setContentType("text/html");
				res.setContent(
					Template.create().fromTemplate(
						content,
						"username", String.valueOf(req.getSession().get("username"))
					)
				);
			    });
		    res.write();
		})
		.get("/logout", (req, res) -> {
		    req.getSession().clear();
		    Cache.create().remove("username");

		    res.setStatus(Status.STATUS_SEE_OTHER);
		    res.getHeaders().addParam(new Param<>("Location", "/html/login.html"));
		    res.write();
		})
		.get("/encrypt", (req, res) -> {
		    String clear = "123456";
		    String key = "1234567812345678";

		    byte[] encrypted = Encrypt.create().encrypt(key.getBytes(), clear.getBytes());
		    byte[] decrypted = Encrypt.create().decrypt(key.getBytes(), encrypted);
		    String response = "TEA( clear: " + clear + " encrypted/decrypted: " + new String(decrypted) + ") \n";

		    encrypted = Encrypt.create(Encrypt.Type.AESEncrypt).encrypt(key.getBytes(), clear.getBytes());
		    decrypted = Encrypt.create(Encrypt.Type.AESEncrypt).decrypt(key.getBytes(), encrypted);
		    response += "AES (clear: " + clear + " encrypted/decrypted: " + new String(decrypted) + ")";

		    res.setContent(response);
		    res.write();
		})
		.get("/setCookie", (req, res) -> {

		    String encodedKey = "EncodedKey";
		    String encodedValue = "EncodedValue1111";
		    req.getSession().put(encodedKey, encodedValue);

		    String encryptedKey = "EncryptedKey";
		    String encryptedValue = "EncryptedValue2222";
		    req.getSession().putEncrypt(encryptedKey, encryptedValue);

		    res.setContent("cookie set");
		    res.write();
		})
		.get("/getCookie", (req, res) -> {

		    String encodedKey = "EncodedKey";
		    String encodedValue = (String) req.getSession().get(encodedKey);

		    String encryptedKey = "EncryptedKey";
		    String encryptedValue = (String) req.getSession().get(encryptedKey);

		    res.setContent(encodedKey + ":" + encodedValue + " - " + encryptedKey + ":" + encryptedValue);
		    res.write();
		})
		.get("/metrics", (req, res) -> {
		    res.setContent(JSON.create().toJson(Metrics.create().getMetrics()));
		    res.write();
		})
		.get("/cache", (req, res) -> {
		    Cache<String, Object> cache = Cache.create("FatJarLocalCache");
		    cache.put("key", "value");
		    if (cache.get("number") == null) {
			cache.put("number", 1);
		    } else {
			cache.put("number", (Integer) cache.get("number") + 1);
		    }
		    Map<String, Object> keyValues = new HashMap<>();
		    keyValues.put("key", cache.get("key"));
		    keyValues.put("number", cache.get("number"));
		    res.setContent(JSON.create().toJson(keyValues));
		    res.write();
		})
		.get("/file", (request, response) -> {
		    String path = File.separator + "tmp";
		    String fileName = "file.temp";
		    String fileContent = "file content here!";
		    boolean result = IO.writeFile(fileContent, path, fileName);
		    if (result) {
			Optional<String> content = IO.readFile(path, fileName);
			if (content.isPresent()) {
			    response.setContent(content.get());
			    response.write();
			} else {
			    throw new Server.ServerException(Status.STATUS_NOT_FOUND, request.getHeader(RequestKeys.URI));
			}
		    } else {
			throw new Server.ServerException(Status.STATUS_INTERNAL_SERVER_ERROR, "could not create file");
		    }
		})
		.get("/db", (req, res) -> {

		    Optional<DB> dbOptional = DB.create();
		    if (dbOptional.isPresent()) {
			DB db = dbOptional.get();
			long numberOfAllEntities = db.count(MyEntity.class);
			if (numberOfAllEntities == 0) {
			    MyEntity myEntity = new MyEntity("five");
			    db.insert(myEntity).ifPresent(System.out::println);
			}
			long numberOfJohnEntities = db.count(MyEntity.class, DB.Query.create("name", "five"));
			long numberOfLargeIDEntities = db.count(MyEntity.class, DB.Query.create("id", DB.Sign.GT, 1L));
			Log.info("numberOfLargeIDEntities: " + numberOfLargeIDEntities);

			List<MyEntity> allEntities = db.findAll(MyEntity.class);
			Log.info("found allEntities: " + allEntities);

			List<MyEntity> entityNameFive = db.find(MyEntity.class, DB.Query.create("name", "five"));
			if (!entityNameFive.isEmpty()) {
			    Log.info("fount entityNameFive: " + entityNameFive);
			}

			List<MyEntity> entityNameFourOrFive = db.find(MyEntity.class, DB.Query.or(
				DB.Query.create("name", "four"),
				DB.Query.create("name", "five")
			));
			if (!entityNameFourOrFive.isEmpty()) {
			    Log.info("fount entityNameFourOrFive: " + entityNameFourOrFive);
			}

			List<MyEntity> entityNameID5AndNameFive = db.find(MyEntity.class, DB.Query.and(
				DB.Query.create("id", 5),
				DB.Query.create("name", "five")
			));
			if (!entityNameID5AndNameFive.isEmpty()) {
			    Log.info("found entityNameID5AndNameFive: " + entityNameID5AndNameFive);
			}

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
			    res.setContent(JSON.create().toJson(insertedEntity));
			} else {
			    res.setContent("could not insert to DB");
			}
			res.write();
		    } else {
			throw new Server.ServerException("no db available");
		    }
		})
		.get("/dbMongo", (req, res) -> {
		    Optional<DB> dbOptional = DB.create(DB.Type.MongoDB);
		    if (dbOptional.isPresent()) {
			DB db = dbOptional.get();
			MyMongoModel model = new MyMongoModel("john", "555-4343", "Elm Street");
			Optional<MyMongoModel> modelOptional = db.insert(model);
			if (modelOptional.isPresent()) {
			    model = modelOptional.get();
			    Log.info("inserted mongo model: " + JSON.create().toJson(model));
			    res.setContent(JSON.create().toJson(model));

			    MyMongoModel foundModel = db.find(MyMongoModel.class, model.getObjectId());
			    Log.info("foundModel: " + foundModel);

			    long numberOfTotalModels = db.count(MyMongoModel.class);
			    Log.info("numberOfTotalModels: " + numberOfTotalModels);

			    db.insert(new MyMongoModel("test", "111-1111", "1st Street"));
			    db.insert(new MyMongoModel("test", "222-2222", "2nd Street"));
			    numberOfTotalModels = db.count(MyMongoModel.class);
			    Log.info("numberOfTotalModels: " + numberOfTotalModels);

			    List<MyMongoModel> mongoModels = db.find(MyMongoModel.class, DB.Query.create("name", "test"));
			    mongoModels.stream().forEach(m -> Log.info(JSON.create().toJson(m)));


			    List<MyMongoModel> allModels = db.findAll(MyMongoModel.class);
			    Log.info(allModels.stream().map(MyMongoModel::getAddress).collect(Collectors.joining(",")));

			    model.setPhone("999-3322");
			    model = db.update(model);
			    Log.info("updated mongo model: " + JSON.create().toJson(model));

			    allModels.forEach(db::delete);
			    Log.info("deleted all mongo model");
			}
			res.write();
		    } else {
			throw new Server.ServerException("no db available");
		    }
		})
		.get("/httpClient", (req, res) -> {
		    try {
			String content = HttpClient.create().url("http://ip.jsontest.com/").method(HttpMethod.GET).send().getContentAsString();
			res.setContent("got content: " + content);
		    } catch (HttpClient.HttpClientException e) {
			res.setContent("got exception: " + e);
		    }
		    res.write();
		})
		.get("/badgeFlat", (req, res) -> {
		    byte[] content = IO.readBinaryFile("target/classes", "heroku-badge-deployed.png").get();
		    res.setContentType("image/png");
		    res.setContentChar(content);
		    res.write();
		})
		.get("/throwException", (req, res) -> {
		    throw new Server.ServerException("tojsonexception");
		})
		.get("/badRequest", (req, res) -> {
		    throw new Server.ServerException(Status.STATUS_BAD_REQUEST);
		})
		.get("/toJSON", (req, res) -> {
		    res.setContent(JSON.create().toJson(new MyPOJO(req.getQueryParam("name", "add a query parameter like '?name=john'"), 101)));
		    res.write();
		})
		.post("/fromJSON", (req, res) -> {
		    MyPOJO myPOJO = JSON.create().fromJson(new String(req.getBody()), MyPOJO.class);
		    res.setContent(JSON.create().toJson(myPOJO));
		    res.write();
		})
		.get("/schedule", (req, res) -> {
		    Schedule.create().add(() -> {
			Log.info("SCHEDULED SCHEDULED SCHEDULED SCHEDULED!!!!");
		    });
		    res.setContentType("text/xml");
		    res.write();
		})
		.get("/toXML", (req, res) -> {
		    Optional<String> xmlOptional = XML.create().toXML(new MyPOJO(req.getQueryParam("name", "add a query parameter like '?name=doe'"), Integer.MAX_VALUE));
		    xmlOptional.ifPresent(res::setContent);
		    res.setContentType("text/xml");
		    res.write();
		})
		.post("/fromXML", (req, res) -> {
		    Optional<MyPOJO> myPOJOOptional = XML.create().fromXML(req.getBody(), MyPOJO.class);
		    myPOJOOptional.ifPresent(myPOJO -> {
			XML.create().toXML(myPOJO).ifPresent(res::setContent);
		    });
		    res.setContentType("text/xml");
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
