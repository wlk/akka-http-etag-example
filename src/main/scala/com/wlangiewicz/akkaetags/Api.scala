package com.wlangiewicz.akkaetags

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

class Api(booksService: BooksService) extends JsonFormats {
  val routes =
    get {
      path("books" / IntNumber) { id =>
        complete {
          booksService.findById(id)
        }
      }
    }
}
