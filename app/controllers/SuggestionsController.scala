package controllers

import javax.inject._

import model.Suggestions
import play.api.libs.json._
import play.api.mvc._
import services.SuggestionsService

import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class SuggestionsController @Inject()(val suggestionsService : SuggestionsService) extends Controller {

  import scala.concurrent.ExecutionContext.Implicits.global

  def index = Action {
    Ok(views.html.suggestions())
  }


  def search(query:String) = Action.async{
   // implicit val suggestionsWrites= Json.writes[Suggestions]
    val future: Future[List[Suggestions]] = suggestionsService.search(query)
    future.map { suggestions =>

      Ok(Json.toJson(suggestions.map(s => s.data):List[String]))
    }
  }
}
