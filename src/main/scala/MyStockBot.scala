import java.awt.Color
import java.nio.file.{Files, Paths}
import java.text.SimpleDateFormat

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s.api.{ChatActions, Polling, _}
import info.mukel.telegrambot4s.api.declarative.{Commands, InlineQueries, _}
import info.mukel.telegrambot4s.methods.{ParseMode, SendPhoto}
import model.{DataCandles, DataISIN, DataStock}
import java.util.{Date, Locale}
import java.time.LocalDate

import scalax.chart.api._
import akka.util.ByteString
import info.mukel.telegrambot4s.models.InputFile
import org.jfree.chart.axis.DateAxis
import org.jfree.data.time._
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.collection.mutable.ListBuffer
import scalax.chart.module.ChartFactories


object MyStockBot extends AuthenticationBot with Polling with InlineQueries with Commands with ChatActions {
  implicit val formats = DefaultFormats

  val important_shares = scala.collection.mutable.Map[Int, scala.collection.mutable.Set[String]] ()

  onCommand('start, 'help) { implicit msg =>
    reply(
      s"""Moscow stock monitoring bot made by ALPHA TEAM.
         |
         |/start | /help - list commands
         |
         |/search args - provides info
         |
         |/login - Login
         |/logout - Logout
         |/list - Only authenticated users have access, shows the list of important shares
         |/add - Only authenticated users have access, adds a share to the list of important shares
         |/delete - Only authenticated users have access, adds a share to the list of important shares
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
              "Изменение: "+f"$dif%1.2f"+ "% \n"
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

  implicit def jdate2jfree(d: Date): RegularTimePeriod = new Second(d)
  onCommand('candles) { implicit msg =>
    withArgs { args =>
      val query = args.mkString(" ")
      val urlJson = Uri("https://iss.moex.com/iss/engines/stock/markets/shares/securities/" + query + "/candles.json")
        .withQuery(Query("from" -> LocalDate.now.minusDays(7).toString))
        .toString()
      for {
        responseJson <- Http().singleRequest(HttpRequest(uri = Uri(urlJson)))
        if responseJson.status.isSuccess()
        json <- Unmarshal(responseJson).to[String]
      } /* do */ {

        val objs = parse(json)
        val input = objs \ "candles" \ "data"
        if(input.isEmpty){
          reply("Wrong query")
        }
        val data = input.extract[List[List[String]]]

        var storage = new ListBuffer[DataCandles]()

        for (d <- data) {
          storage += DataCandles(d(0), d(1), d(2), d(3), d(4), d(5), d(6), d(7))
        }


        var min = storage(0).close.toFloat
        var max = storage(0).close.toFloat

        //        val dataset = new TimeSeriesCollection()
        //        val series = new TimeSeries("Series")

        val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var test = List[(Date, Float)]()

        //        var result = scala.collection.mutable.Map[Date, Float]()
        for (s <- storage) {
          test = ((format.parse(s.end)) -> s.close.toFloat) :: test
          if (s.close.toFloat < min) {
            min = s.close.toFloat
          }
          if (s.close.toFloat > max) {
            max = s.close.toFloat
          }
        }

        val seri = test.toTimeSeries("Price")
        val chart = XYLineChart(seri)
        chart.plot.getRangeAxis.setLowerBound(min)
        chart.plot.getRangeAxis.setUpperBound(max)
        chart.plot.setBackgroundPaint(Color.WHITE)
        chart.plot.setDomainGridlinePaint(Color.black)
        chart.plot.setRangeGridlinePaint(Color.black)
        chart.plot.setBackgroundAlpha(0.5f)
        chart.title = query
        val axis = chart.plot.getDomainAxis.asInstanceOf[DateAxis]
        axis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yyyy", new Locale("eu", "EU")))

        chart.saveAsPNG("chart.png")

        reply("done")

        val photo = InputFile(Paths.get("chart.png"))
        uploadingPhoto // Hint the user
        request(SendPhoto(msg.source, photo))

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


  onCommand('list) { implicit msg =>
    authenticatedOrElse {
      admin =>
        reply(
          s"""${admin.firstName}
             |${important_shares(admin.id).mkString("\n")}
             """.stripMargin)
    } /* or else */ {
      user =>
        reply(s"${user.firstName}, you must /login first.")
    }
  }


  onCommand('add) { implicit msg =>
    authenticatedOrElse {
      admin => {

        withArgs { args =>
          val shares = args.mkString(" ").split(" ")
          if(shares.nonEmpty)
            important_shares(admin.id) ++= shares
        }

        reply(
          s"""${admin.firstName}
             |${important_shares(admin.id).mkString("\n")}
             """.stripMargin)
      }
    } /* or else */ {
      user =>
        reply(s"${user.firstName}, you must /login first.")
    }
  }


  onCommand('delete) { implicit msg =>
    authenticatedOrElse {
      admin => {

        withArgs { args =>
          val shares = args.mkString(" ").split(" ")
          if(shares.nonEmpty)
            important_shares(admin.id) --= shares
        }

        reply(
          s"""${admin.firstName}
             |${important_shares(admin.id).mkString("\n")}
             """.stripMargin)
      }
    } /* or else */ {
      user =>
        reply(s"${user.firstName}, you must /login first.")
    }
  }

  def moexUrl(query: String): String =
    Uri("https://iss.moex.com/iss/securities.json")
      .withQuery(Query("q" -> query))
      .toString()
}