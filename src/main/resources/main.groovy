import fatjar.Log
import fatjar.Server

public class Main {

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        Server.create()
                .listen(8080, "0.0.0.0")
                .filter("/*",
                { req, res ->
                    Log.info("/Hi filter called");
                })
                .get("/",
                { req, res ->
                    res.setContent("Welcome");
                    res.write();
                })
                .post("/", { req, res -> })
                .delete("/", { req, res -> })
                .put("/", { req, res -> })
                .start();
    }


}
