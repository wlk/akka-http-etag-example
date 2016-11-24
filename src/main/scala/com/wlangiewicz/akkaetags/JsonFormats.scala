package com.wlangiewicz.akkaetags

import spray.json.DefaultJsonProtocol

class JsonFormats extends DefaultJsonProtocol {
  implicit val bookFormat = jsonFormat3(Book)
}