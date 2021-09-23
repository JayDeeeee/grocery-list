package com.example

import com.example.ItemRegistry.ActionPerformed

//#json-formats
import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val itemJsonFormat = jsonFormat1(Item)
  implicit val itemsJsonFormat = jsonFormat1(Items)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
//#json-formats
