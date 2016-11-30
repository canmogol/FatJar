package sample

import fatjar.dto.Status._
import fatjar.dto.{Request, RequestKeys, Response}
import fatjar.{Log, Server}


object ScalaMain {
  def main(args: Array[String]): Unit = {
    Server.create()

      .listen(8080, "0.0.0.0")

      .register(STATUS_BAD_REQUEST, (req: Request, res: Response) => {
        res.setContentType("text/html")
        res.setContent("<h1>BAD REQUEST!</h1>")
        res.write()
      })

      .filter("/*", (req: Request, res: Response) => {
        Log.info("Wildcard filter called")
      })

      .filter("/aa/*", (req: Request, res: Response) => {
        val uri = req.getHeader(RequestKeys.URI)
        if (req.getSession == null || req.getSession.get("username") == null) {
          throw new Server.ServerException(STATUS_UNAUTHORIZED, "unauthorized call to " + uri)
        }
      })

      .filter("/Hi", (req: Request, res: Response) => {
        Log.info("/Hi filter called")
      })

      .get("/", (req: Request, res: Response) => {
        res.setContent("Welcome")
        res.write()
      })

      .get("/Hi", (req: Request, res: Response) => {
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
