package com.wlangiewicz.akkaetags

import java.security.MessageDigest

import akka.http.scaladsl.model.DateTime
import akka.http.scaladsl.model.headers.EntityTag

private[akkaetags] object MD5 {
  private val md5 = MessageDigest.getInstance("MD5")

  private[akkaetags] def md5sum(value: String) = {
    md5.digest(value.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }
}

trait ETags {
  // Note that in both cases we are using only `lastUpdated` to calculate ETag
  def bookETag(book: Book): EntityTag = {
    EntityTag(MD5.md5sum(book.lastUpdated.toIsoDateTimeString), weak = true)
  }

  def lightweightBookETag(lastUpdated: DateTime): EntityTag = {
    EntityTag(MD5.md5sum(lastUpdated.toIsoDateTimeString), weak = true)
  }
}