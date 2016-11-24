package com.wlangiewicz.akkaetags

class BooksService {
  def findById(id: Int): Book = {
    Book(1, "Lord of the Rings", "J. R. R. Tolkien")
  }
}
