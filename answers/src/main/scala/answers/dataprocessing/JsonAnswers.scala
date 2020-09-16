package answers.dataprocessing

object JsonAnswers {

  sealed trait Json
  case class JsonNumber(number: Double)         extends Json
  case class JsonString(text: String)           extends Json
  case class JsonObject(obj: Map[String, Json]) extends Json

  def trimAll(json: Json): Json =
    json match {
      case _: JsonNumber   => json
      case JsonString(str) => JsonString(str.trim)
      case JsonObject(obj) =>
        JsonObject(obj.map {
          case (key, value) => key -> trimAll(value)
        })
    }

  def anonymize(json: Json): Json =
    json match {
      case _: JsonNumber => JsonNumber(0)
      case _: JsonString => JsonString("***")
      case JsonObject(obj) =>
        JsonObject(obj.map {
          case (key, value) =>
            key -> anonymize(value)
        })
    }

  def search(json: Json, searchText: String): Boolean =
    json match {
      case _: JsonNumber    => false
      case JsonString(text) => text.contains(searchText)
      case JsonObject(obj)  => obj.values.exists(search(_, searchText))
    }

  def depth(json: Json): Int =
    json match {
      case _: JsonNumber | _: JsonString =>
        0
      case JsonObject(obj) =>
        obj.values.map(depth).maxOption.fold(0)(_ + 1)
    }

}
