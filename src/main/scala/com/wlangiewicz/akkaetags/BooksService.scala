package com.wlangiewicz.akkaetags

import akka.http.scaladsl.model.DateTime

class BooksService {

  private val books = Vector(
    Book(1, "Lord of the Rings", "J. R. R. Tolkien", DateTime.now)
  )

  def findById(id: Int): Option[Book] = {
    books.find(b => b.id == id)
  }
}
