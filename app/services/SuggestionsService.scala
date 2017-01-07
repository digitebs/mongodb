package services;

import com.google.inject.Inject
import model.Suggestions
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver, QueryOpts}
import reactivemongo.bson._
import scala.util.matching._

import scala.concurrent.{ExecutionContext, Future}

class SuggestionsService @Inject()(val reactiveMongoApi:ReactiveMongoApi) {
  // My settings (see available connection options)
  //val mongoUri = "mongodb://localhost:27017/local?authMode=scram-sha1"

  import ExecutionContext.Implicits.global // use any appropriate context

  def suggestionCollection:Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("suggestions"))
  // Write Documents: insert or update

  implicit def suggestionsWriter: BSONDocumentWriter[Suggestions] = Macros.writer[Suggestions]
  // or provide a custom one
  implicit def suggestionsReader: BSONDocumentReader[Suggestions] = Macros.reader[Suggestions]

  /**
  def search(text: String): Future[List[Suggestions]] =     suggestionCollection.flatMap(_.find(
  document("$text" -> document("$search" -> String.format("\"%s\"",text))),
  document("score" -> document("$meta" -> "textScore"))).
  sort(document("score" -> document("$meta" -> "textScore"))). // query builder
    cursor[Suggestions]().collect[List]())// use personWriter
  **/

  def search(text: String): Future[List[Suggestions]] =     suggestionCollection.flatMap(_.find(
    document("data" -> document("$regex" -> String.format("^%s.*",Regex.quote(text)),"$options"->"i"))).sort(document("data"->1)).options(QueryOpts().batchSize(5)).
    cursor[Suggestions]().collect[List](5))// use personWriter


}

