package com.wlangiewicz.akkaetags

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.server.Route

class BooksApi(booksService: BooksService) extends JsonFormats with ETags {
  val routes: Route =
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
          optionalHeaderValueByName("If-None-Match") {
            case Some(_) =>
              booksService.getBookLastUpdatedById(id) match {
                case Some(lastUpdated) =>
                  conditional(lightweightBookETag(lastUpdated), lastUpdated) {
                    complete {
                      booksService.findById(id) match {
                        case Some(book) => book
                        case None       => HttpResponse(NotFound, entity = "Not found, but this should be caught earlier")
                      }
                    }
                  }
                case None => complete {
                  HttpResponse(NotFound, entity = "Not found")
                }
              }
            case None =>
              booksService.findById(id) match {
                case Some(book) =>
                  conditional(bookETag(book), book.lastUpdated) {
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
}
