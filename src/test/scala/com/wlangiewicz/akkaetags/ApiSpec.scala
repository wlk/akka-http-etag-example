package com.wlangiewicz.akkaetags

import org.scalatest.{FlatSpec, Matchers}
import akka.http.scaladsl.model.StatusCodes.{OK, NotFound, NotModified}
import akka.http.scaladsl.testkit.ScalatestRouteTest

class ApiSpec extends FlatSpec with Matchers with ScalatestRouteTest {
  val api = new BooksApi(new BooksService)

  "/books endpoint" should "return book without setting HTTP headers" in {
    Get("/books/1") ~> api.routes ~> check {
      status shouldBe OK
      header("Last-Modified") shouldBe None
      header("ETag") shouldBe None
    }
  }

  it should "return 404 without setting HTTP headers" in {
    Get("/books/999999") ~> api.routes ~> check {
      status shouldBe NotFound
      header("Last-Modified") shouldBe None
      header("ETag") shouldBe None
    }
  }

  "/books-etags endpoint" should "return 404 without setting HTTP headers" in {
    Get("/books-etags/999999") ~> api.routes ~> check {
      status shouldBe NotFound
      header("Last-Modified") shouldBe None
      header("ETag") shouldBe None
    }
  }

  val LotrEtag = """W/"1e8ec132952ddfe628c6e2ff6a66d843""""
  val LotrLastUpdated = "Tue, 25 Oct 2016 12:45:00 GMT"

  it should "return book and set HTTP headers when no ETag or If-Modified-Since is provided" in {
    Get("/books-etags/1") ~> api.routes ~> check {
      status shouldBe OK
      header("Last-Modified").get.value shouldBe LotrLastUpdated
      header("ETag").get.value shouldBe LotrEtag
    }
  }

  it should "return book and set HTTP headers when invalid ETag is provided" in {
    Get("/books-etags/1") ~> addHeader("If-None-Match", """W/"ABC"""") ~> api.routes ~> check {
      status shouldBe OK
      header("Last-Modified").get.value shouldBe LotrLastUpdated
      header("ETag").get.value shouldBe LotrEtag
    }
  }

  it should "return 304 when valid ETag is provided is provided" in {
    Get("/books-etags/1") ~> addHeader("If-None-Match", LotrEtag) ~> api.routes ~> check {
      status shouldBe NotModified
    }
  }

  it should "return book and set HTTP headers when earlier If-Modified-Since is provided" in {
    Get("/books-etags/1") ~> addHeader("If-Modified-Since", "Mon, 24 Oct 2016 12:45:00 GMT") ~> api.routes ~> check {
      status shouldBe OK
      header("Last-Modified").get.value shouldBe LotrLastUpdated
      header("ETag").get.value shouldBe LotrEtag
    }
  }

  it should "return 304 when valid If-Modified-Since is provided is provided" in {
    Get("/books-etags/1") ~> addHeader("If-Modified-Since", LotrLastUpdated) ~> api.routes ~> check {
      status shouldBe NotModified
    }
  }
}
