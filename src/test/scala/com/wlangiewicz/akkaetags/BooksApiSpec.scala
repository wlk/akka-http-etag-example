package com.wlangiewicz.akkaetags

import org.scalatest.{FlatSpec, Matchers}
import akka.http.scaladsl.model.StatusCodes.{NotFound, NotModified, OK}
import akka.http.scaladsl.testkit.ScalatestRouteTest

class BooksApiSpec extends FlatSpec with Matchers with ScalatestRouteTest {
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

  val LotrEtag = """W/"1e8ec132952ddfe628c6e2ff6a66d843""""
  val LotrLastUpdated = "Tue, 25 Oct 2016 12:45:00 GMT"

  def time(block: => Any): Int = {
    val t0 = System.currentTimeMillis()
    block
    val t1 = System.currentTimeMillis()
    (t1 - t0).toInt
  }

  "/books-etags endpoint" should "return 404 without setting HTTP headers" in {
    time(Get("/books-etags/999999") ~> api.routes ~> check {
      status shouldBe NotFound
      header("Last-Modified") shouldBe None
      header("ETag") shouldBe None
    }) should be > 1000
  }

  it should "return book and set HTTP headers when no ETag or If-Modified-Since is provided" in {
    time(Get("/books-etags/1") ~> api.routes ~> check {
      status shouldBe OK
      header("Last-Modified").get.value shouldBe LotrLastUpdated
      header("ETag").get.value shouldBe LotrEtag
    }) should be > 1000
  }

  it should "return book and set HTTP headers when invalid ETag is provided" in {
    time(Get("/books-etags/1") ~> addHeader("If-None-Match", """W/"ABC"""") ~> api.routes ~> check {
      status shouldBe OK
      header("Last-Modified").get.value shouldBe LotrLastUpdated
      header("ETag").get.value shouldBe LotrEtag
    }) should be > 1000

  }

  it should "return 304 when valid ETag is provided is provided" in {
    time(Get("/books-etags/1") ~> addHeader("If-None-Match", LotrEtag) ~> api.routes ~> check {
      status shouldBe NotModified
    }) should be < 20
  }

  it should "return book and set HTTP headers when earlier If-Modified-Since is provided" in {
    time(Get("/books-etags/1") ~> addHeader("If-Modified-Since", "Mon, 24 Oct 2016 12:45:00 GMT") ~> api.routes ~> check {
      status shouldBe OK
      header("Last-Modified").get.value shouldBe LotrLastUpdated
      header("ETag").get.value shouldBe LotrEtag
    }) should be > 1000
  }

  // Fast path not implemented for "If-Modified-Since" header
  it should "return 304 when valid If-Modified-Since is provided is provided" in {
    time(Get("/books-etags/1") ~> addHeader("If-Modified-Since", LotrLastUpdated) ~> api.routes ~> check {
      status shouldBe NotModified
    }) should be > 1000
  }
}
