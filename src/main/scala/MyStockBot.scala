import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.api.declarative._
import info.mukel.telegrambot4s.methods.ParseMode
import model.{DataISIN, DataStock}
import java.util.Date
import java.time.LocalDate

import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.collection.mutable.ListBuffer

object MyStockBot extends TelegramBot with Polling with InlineQueries with Commands {
  implicit val formats = DefaultFormats

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

  onCommand('sv) { implicit msg =>
    withArgs { args =>
      val query = args.mkString(" ")

      val url = Uri("https://iss.moex.com/iss/history/engines/stock/markets/shares/securities/"+query+".json")
        .withQuery(Query("from" -> LocalDate.now.minusDays(1).toString))
        .toString()
      print(url)
      for {
        response <- Http().singleRequest(HttpRequest(uri = Uri(url)))
        if response.status.isSuccess()
        json <- Unmarshal(response).to[String]
      } /* do */ {
        print(json)
        val objs = parse(json)
        val input = objs \ "history" \ "data"
        val data = input.extract[List[List[String]]]

        var storage = new ListBuffer[DataStock]()
        for(d <-data){
          storage+= DataStock(d(0),d(1),d(2),d(3),d(4),d(5),d(6),d(7),d(8),d(9),d(10),d(11),d(12),d(13),d(14),d(15),d(16),d(17),d(18),d(19))
        }
        var result = ""
        for(s <- storage){
          if(s.boardid == "TQBR"){
            val dif = ((s.marketprice2.toFloat/s.open.toFloat - 1)*100)
            result += s.secid + " - " + s.shortname + "\n \n" +
              "Открытие: "+ s.open + "\n" +
              "Текущая цена: " + s.marketprice2 + " \n" +
              "Изменение: "+f"$dif%1.2f"+ "\n"
          }
        }
        if(result==""){
          reply("Bad request, you probably mistyped secid")
        }else{
          reply(result)
        }

      }
    }
  }
  onCommand('search) { implicit msg =>
    withArgs { args =>
      val query = args.mkString(" ")
      val url = Uri("https://iss.moex.com/iss/securities.json")
        .withQuery(Query("q" -> query))
        .toString()
      for {
        response <- Http().singleRequest(HttpRequest(uri = Uri(url)))
        if response.status.isSuccess()
        json <- Unmarshal(response).to[String]
      } /* do */ {

        val objs = parse(json)
        val input = objs \ "securities" \ "data"
        val data = input.extract[List[List[String]]]

        var storage = new ListBuffer[DataISIN]()
        for(d <-data){
          storage+= DataISIN(d(0),d(1),d(2),d(3),d(4),d(5),d(6),d(7),d(8),d(9),d(10),d(11),d(12),d(13),d(14),d(15))
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

}