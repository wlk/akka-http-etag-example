package com.wlangiewicz.akkaetags

import akka.http.scaladsl.model.DateTime

case class Book(id: Int, name: String, author: String, lastUpdated: DateTime)