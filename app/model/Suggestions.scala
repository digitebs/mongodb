package model

import play.api.libs.json.Json

case class Suggestions(data: String)

object Suggestions{
  implicit val suggestionsWrites= Json.writes[Suggestions]
}