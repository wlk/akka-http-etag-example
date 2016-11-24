package com.wlangiewicz.akkaetags

import org.scalatest.{FlatSpec, Matchers}
import akka.http.scaladsl.model.StatusCodes.{OK, NotFound}
import akka.http.scaladsl.testkit.ScalatestRouteTest

class ApiSpec extends FlatSpec with Matchers with ScalatestRouteTest {
  val api = new Api(new BooksService)

  "API" should "return book" in {
    Get("/books/1") ~> api.routes ~> check {
      status shouldBe OK
    }

    Get("/books/2") ~> api.routes ~> check {
      status shouldBe NotFound
    }
  }
}
