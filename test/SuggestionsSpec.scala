import com.github.simplyscala.{MongoEmbedDatabase, MongodProps}
import de.flapdoodle.embed.mongo.distribution.Version
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._
import services.SuggestionsService

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  * For more information, consult the wiki.
  *
  * ensure theres no local mongo db instance running on local machine
  *
  */
class SuggestionsSpec extends PlaySpec
  with OneAppPerSuite
  with ScalaFutures
  with MongoEmbedDatabase
  with BeforeAndAfterAll {

  implicit val defaultPatience =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  import ExecutionContext.Implicits.global
  // use any appropriate context

  var mongoProps: MongodProps = null

  var suggestionsService:SuggestionsService = null

  override def beforeAll() = {
    mongoProps = mongoStart(27017, Version.V2_7_1) // by default port = 12345 & version = Version.2.3.0
    val api = app.injector.instanceOf[ReactiveMongoApi]
    suggestionsService = app.injector.instanceOf[SuggestionsService]
    val suggestionCollection: Future[BSONCollection] = api.database.map(_.collection("suggestions"))
    val sync =Await.result(suggestionCollection, 10 seconds)
      // m.create()
    sync.insert(document("data" -> "a"))
    sync.insert(document("data" -> "ab"))
    sync.insert(document("data" -> "acc"))
    sync.insert(document("data" -> "accc"))
    sync.insert(document("data" -> "acdef"))
  } // add your own port & version parameters in mongoStart method if you need it

  override def afterAll() = {
    mongoStop(mongoProps)
  }

  "search text" when {

    "single letter" should {
      "have length 5" in {
        whenReady(suggestionsService.search("a")) {
          _ must have length 5
        }
      }
    }

    "empty" should {
      "have length 5" in {
        whenReady(suggestionsService.search("")) {
          _ must have length 5
        }
      }
    }

    "two letter" should {
      "have single result" in {
        whenReady(suggestionsService.search("ab")) {
          _ must have length 1
        }
      }
    }

    "mix with regex" should {
      "return empty" in {
        whenReady(suggestionsService.search("a.*")) {
          _ must have length 0
        }
      }
    }
  }
}
