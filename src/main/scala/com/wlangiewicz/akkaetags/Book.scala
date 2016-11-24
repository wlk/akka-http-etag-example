package com.wlangiewicz.akkaetags

import akka.http.scaladsl.model.DateTime
import akka.http.scaladsl.model.headers.EntityTag

case class Book(id: Int, name: String, author: String, lastUpdated: DateTime)

/*
object Book {
  def eTag: EntityTag = {

  }
}
*/
