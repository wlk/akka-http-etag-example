package com.wlangiewicz.akkaetags

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.server.Route

class BooksApi(booksService: BooksService) extends JsonFormats with ETags {
  val routes: Route =
    get {
      // This is the implementation of the same endpoint that doesn't use ETags of Last-Modified optimizations, it will always fetch full data from the DB
      // But it's also much more easier to understand
      path("books" / IntNumber) { id =>
        complete {
          booksService.findById(id) match {
            case Some(book) => book
            case None       => HttpResponse(NotFound, entity = "Not found")

          }
        }
      } ~
        // There are 3 cases to consider
        path("books-etags" / IntNumber) { id =>
          optionalHeaderValueByName("If-None-Match") {
            case Some(_) =>
              // First case, we get request with some value of "If-None-Match" header, right now we are unable to say if it's valid ETag
              booksService.getBookLastUpdatedById(id) match {
                case Some(lastUpdated) =>
                  // "conditional" directive receives the ETag extracted from the request, and compares it to "lightweightBookETag(lastUpdated)"
                  // which was calculated only based on the date of the book - we didn't have to fetch full object from the DB
                  conditional(lightweightBookETag(lastUpdated), lastUpdated) {
                    // "conditional" directive will return 304 if ETag or "If-Modified-Since" were valid, in this case we don't need to fetch anything more from DB
                    complete {
                      // If ETag was invalid (for example outdated), we continue, this time fetching full object from DB (which is more time consuming) and return it normally
                      booksService.findById(id) match {
                        case Some(book) => book
                        case None       => throw new RuntimeException("This shouldn't happen")
                      }
                    }
                  }
                case None => complete {
                  // If resource doesn't exist we don't set any headers
                  HttpResponse(NotFound, entity = "Not found")
                }
              }

            case None =>
              // Second case, request doesn't contain "If-None-Match" header, we know that we have to return 200 with full body, so we do that (alternatively we return 404 if resource wasn't found)
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
