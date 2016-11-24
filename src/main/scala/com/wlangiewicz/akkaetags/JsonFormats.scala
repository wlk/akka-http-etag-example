package com.wlangiewicz.akkaetags

import akka.http.scaladsl.model.DateTime
import spray.json.DefaultJsonProtocol

trait JsonFormats extends DefaultJsonProtocol {
  implicit val dateTimeFormat = jsonFormat9(DateTime.apply)
  implicit val bookFormat = jsonFormat4(Book)
}