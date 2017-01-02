package controllers

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

object HelloController {
  def getHello() = {
    complete(
      HttpEntity(
        ContentTypes.`text/html(UTF-8)`,
        "<h1>Say hello to akka-http</h1>"
      )
    )
  }
}
