import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.api.declarative._
import info.mukel.telegrambot4s.methods.ParseMode
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.collection.mutable.ListBuffer


object MyStockBot extends TelegramBot with Polling with InlineQueries with Commands {
  implicit val formats = DefaultFormats
  case class Data(id:String,
                  secid:String,
                  shortname:String,
                  regnumber:String,
                  name:String,
                  isin:String,
                  is_traded:String,
                  emitend_id:String,
                  emitent_title:String,
                  emitent_inn:String,
                  emitent_okpo:String,
                  gosreg:String,
                  _type:String,
                  group:String,
                  primary_boardid:String,
                  marketprice_boardid:String)

  override def token: String = "564399150:AAESOLtP7aKK3G9BphDCwRtXHh-4es1x7Kc"

  onCommand('start, 'help) { implicit msg =>
    reply(
      s"""Moscow stock monitoring bot made by ALPHA TEAM.
         |
         |/start | /help - list commands
         |
         |/search args - provides info
         |
         |@Bot args - Inline mode
      """.stripMargin,
      parseMode = ParseMode.Markdown)
  }


  onCommand('search) { implicit msg =>
    withArgs { args =>
      val query = args.mkString(" ")
      val url = moexUrl(query)
      for {
        response <- Http().singleRequest(HttpRequest(uri = Uri(url)))
        if response.status.isSuccess()
        json <- Unmarshal(response).to[String]
      } /* do */ {

        val objs = parse(json)
        val input = objs \ "securities" \ "data"
        val data = input.extract[List[List[String]]]

        var storage = new ListBuffer[Data]()
        for(d <-data){
          storage+= Data(d(0),d(1),d(2),d(3),d(4),d(5),d(6),d(7),d(8),d(9),d(10),d(11),d(12),d(13),d(14),d(15))
        }
        var result = ""
        for(s <- storage){
          if(s.marketprice_boardid == "TQBR"){
            result += s.secid + " - " + s.name + "\n"
          }
        }
        reply(result)
      }
    }
  }

  def moexUrl(query: String): String =
    Uri("https://iss.moex.com/iss/securities.json")
      .withQuery(Query("q" -> query))
      .toString()
}