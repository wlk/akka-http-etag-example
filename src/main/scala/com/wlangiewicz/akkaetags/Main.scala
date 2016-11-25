package com.wlangiewicz.akkaetags

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Main extends App {
  implicit val system = ActorSystem("articles")
  implicit val dispatcher = system.dispatcher

  implicit val materializer = ActorMaterializer()

  val booksService = new BooksService()

  val api = new BooksApi(booksService)

  val server = Http().bindAndHandle(api.routes, interface = "0.0.0.0", port = 8080)

}
