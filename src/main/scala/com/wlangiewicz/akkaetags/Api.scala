package com.wlangiewicz.akkaetags

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.NotFound

class Api(booksService: BooksService) extends JsonFormats with ETags {
  val routes =
    get {
      path("books" / IntNumber) { id =>
        complete {
          booksService.findById(id) match {
            case Some(book) => book
            case None       => HttpResponse(NotFound, entity = "Not found")

          }
        }
      } ~
        path("books-etags" / IntNumber) { id =>
          booksService.findById(id) match {
            case Some(book) =>
              conditional(book.eTag, book.lastUpdated) {
                complete {
                  book
                }
              }
            case None =>
              complete {
                HttpResponse(NotFound, entity = "Not found")
              }
          }
        }
    }
}
