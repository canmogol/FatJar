package sample

import fatjar.dto.RequestKeys._
import fatjar.dto.Status._
import fatjar.{Log, Server}


object ScalaMain {
  def main(args: Array[String]): Unit = {
    Server.create()

      .listen(8080, "0.0.0.0")

      .register(STATUS_BAD_REQUEST, (req, res) => {
        res.setContentType("text/html")
        res.setContent("<h1>BAD REQUEST!</h1>")
        res.write()
      })

      .filter("/*", (req, res) => {
        Log.info("Wildcard filter called")
      })

      .filter("/aa/*", (req, res) => {
        val uri = req.getHeader(URI)
        if (req.getSession == null || req.getSession.get("username") == null) {
          throw new Server.ServerException(STATUS_UNAUTHORIZED, "unauthorized call to " + uri)
        }
      })

      .filter("/Hi", (req, res) => {
        Log.info("/Hi filter called")
      })

      .get("/", (req, res) => {
        res.setContent("Welcome Scala")
        res.write()
      })

      .get("/Hi", (req, res) => {
        if (req.getQueryParams.containsKey("name")) {
          res.setContent("Hello " + req.getQueryParams.getValue("name"))
        } else {
          res.setContent("type \"http://localhost:80/Hi?name=john\" in your browser")
        }
        res.write()
      })

      .start()
  }
}
