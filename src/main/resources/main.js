var HashMap = Java.type('java.util.HashMap');
var Param = Java.type('fatjar.dto.Param');
var Status = Java.type('fatjar.dto.Status');
var RequestKeys = Java.type('fatjar.dto.RequestKeys');
var Cache = Java.type('fatjar.Cache');
var Template = Java.type('fatjar.Template');
var IO = Java.type('fatjar.IO');
var Log = Java.type('fatjar.Log');
var Server = Java.type('fatjar.Server');

var map = new HashMap();
map.put(Server.ServerParams.SIGN_KEY, "1234567812345678");
map.put(Server.ServerParams.APPLICATION_NAME, "FAT_JAR_EXAMPLE_APP");

Server.create(map)
    .listen(8080, "0.0.0.0")
    .register(Status.STATUS_BAD_REQUEST, function (req, res) {
        res.setContentType("text/html");
        res.setContent("<h1>BAD REQUEST!</h1>");
        res.write();
    })
    .filter("/*", function (req, res) {
        Log.info("Wildcard filter called");
    })
    .filter("/aa/*", function (req, res) {
        var uri = req.getHeader(RequestKeys.URI);
        if (req.getSession() == null || req.getSession().get("username") == null) {
            res.setStatus(Status.STATUS_SEE_OTHER);
            res.getHeaders().addParam(new Param("Location", "/html/login.html"));
            res.write();
        }
    })
    .filter("/Hi", function (req, res) {
        Log.info("/Hi filter called");
    })
    .get("/", function (req, res) {
        res.setContent("Welcome");
        res.write();
    })
    .get("/Hi", function (req, res) {
        if (req.getQueryParams().containsKey("name")) {
            res.setContent("Hello " + req.getQueryParams().getValue("name"));
        } else {
            res.setContent("type \"http://localhost:80/Hi?name=john\" in your browser");
        }
        res.write();
    })
    .get("/@folder/@file", function (req, res) {
        uri = req.getHeader(RequestKeys.URI);
        res.setContentType(req.getMimeType(uri));
        var content = IO.readResource(req.getQueryParam("@folder"), req.getQueryParam("@file"));
        if (content.isPresent()) {
            res.setContent(content.get());
        } else {
            var contentOptional = IO.readBinaryResource(req.getQueryParam("@folder"), req.getQueryParam("@file"));
            if (contentOptional.isPresent()) {
                res.setContentChar(contentOptional.get());
            } else {
                throw new Server.ServerException(Status.STATUS_NOT_FOUND, "no file found with uri: " + uri);
            }
        }
        res.write();
    })
    .post("/login", function (req, res) {
        if (!req.hasPostParams("username", "password")) {
            IO.readResource("template", "freemarker", "error.ftl")
                .ifPresent(function (content) {
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
                .ifPresent(function (content) {
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
            res.getHeaders().addParam(new Param("Location", "/aa/logged"));
            Cache.create().put("username", req.getPostParam("username"));
        }
        res.write();
    })
    .get("/aa/logged", function (req, res) {
        IO.readResource("template", "freemarker", "login.ftl")
            .ifPresent(function (content) {
                res.setContentType("text/html");
                res.setContent(
                    Template.create().fromTemplate(
                        content,
                        "username", "" + req.getSession().get("username")
                    )
                );
            });
        res.write();
    })
    .get("/logout", function (req, res) {
        req.getSession().clear();
        Cache.create().remove("username");

        res.setStatus(Status.STATUS_SEE_OTHER);
        res.getHeaders().addParam(new Param("Location", "/html/login.html"));
        res.write();
    })
    .start();
