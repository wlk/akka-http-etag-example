package com.wlangiewicz.akkaetags

import java.security.MessageDigest

import akka.http.scaladsl.model.headers.EntityTag

import scala.language.implicitConversions

private[akkaetags] object MD5 {
  private val md5 = MessageDigest.getInstance("MD5")

  private[akkaetags] def md5sum(value: String) = {
    md5.digest(value.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }
}

trait ETags {
  class ETaggedBook(book: Book) {
    def eTag = EntityTag(MD5.md5sum(book.author + book.id + book.lastUpdated.toIsoDateTimeString + book.name), weak = true)
  }

  implicit def eTag(book: Book): ETaggedBook = new ETaggedBook(book)

}