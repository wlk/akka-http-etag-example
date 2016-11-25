package com.wlangiewicz.akkaetags

import akka.http.scaladsl.model.DateTime

class BooksService {

  private val books = Vector(
    Book(1, "Lord of the Rings", "J. R. R. Tolkien", DateTime(2016, 10, 25, 12, 45, 0)),
    Book(2, "Akka in Action", "Rob Bakker, Raymond Roestenburg, Rob Williams", DateTime(2016, 10, 21, 12, 45, 0))
  )

  def getBookLastUpdatedById(id: Int): Option[DateTime] = {
    // this is a lightweight operation that fetches only DateTime for specific book
    books.find(b => b.id == id).map(_.lastUpdated)
  }

  def findById(id: Int): Option[Book] = {
    // this simulates a time consuming object fetching
    Thread.sleep(1000)
    books.find(b => b.id == id)
  }
}
