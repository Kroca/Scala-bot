import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.collection.mutable.ListBuffer

object jsonattempt2 {
  implicit val formats = DefaultFormats
  val json = "{\n\"securities\": {\n\t\"metadata\": {\n\t\t\"id\": {\"type\": \"int32\"},\n\t\t\"secid\": {\"type\": \"string\", \"bytes\": 36, \"max_size\": 0},\n\t\t\"shortname\": {\"type\": \"string\", \"bytes\": 189, \"max_size\": 0},\n\t\t\"regnumber\": {\"type\": \"string\", \"bytes\": 189, \"max_size\": 0},\n\t\t\"name\": {\"type\": \"string\", \"bytes\": 765, \"max_size\": 0},\n\t\t\"isin\": {\"type\": \"string\", \"bytes\": 765, \"max_size\": 0},\n\t\t\"is_traded\": {\"type\": \"int32\"},\n\t\t\"emitent_id\": {\"type\": \"int32\"},\n\t\t\"emitent_title\": {\"type\": \"string\", \"bytes\": 765, \"max_size\": 0},\n\t\t\"emitent_inn\": {\"type\": \"string\", \"bytes\": 30, \"max_size\": 0},\n\t\t\"emitent_okpo\": {\"type\": \"string\", \"bytes\": 24, \"max_size\": 0},\n\t\t\"gosreg\": {\"type\": \"string\", \"bytes\": 189, \"max_size\": 0},\n\t\t\"type\": {\"type\": \"string\", \"bytes\": 93, \"max_size\": 0},\n\t\t\"group\": {\"type\": \"string\", \"bytes\": 93, \"max_size\": 0},\n\t\t\"primary_boardid\": {\"type\": \"string\", \"bytes\": 12, \"max_size\": 0},\n\t\t\"marketprice_boardid\": {\"type\": \"string\", \"bytes\": 12, \"max_size\": 0}\n\t},\n\t\"columns\": [\"id\", \"secid\", \"shortname\", \"regnumber\", \"name\", \"isin\", \"is_traded\", \"emitent_id\", \"emitent_title\", \"emitent_inn\", \"emitent_okpo\", \"gosreg\", \"type\", \"group\", \"primary_boardid\", \"marketprice_boardid\"], \n\t\"data\": [\n\t\t[5443, \"SBER\", \"Сбербанк\", \"10301481B\", \"Сбербанк России ПАО ао\", \"RU0009029540\", 1, 1199, \"Публичное акционерное общество \\\"Сбербанк России\\\"\", \"7707083893\", \"00032537\", \"10301481B\", \"common_share\", \"stock_shares\", \"TQBR\", \"TQBR\"],\n\t\t[5444, \"SBERP\", \"Сбербанк-п\", \"20301481B\", \"Сбербанк России ПАО ап\", \"RU0009029557\", 1, 1199, \"Публичное акционерное общество \\\"Сбербанк России\\\"\", \"7707083893\", \"00032537\", \"20301481B\", \"preferred_share\", \"stock_shares\", \"TQBR\", \"TQBR\"],\n\t\t[3052, \"RU000A0ERGA7\", \"ПИФСбер-КН\", \"0252-74113866\", \"ПИФСбербанк Комм.недвижимость\", \"RU000A0ERGA7\", 1, 1814, \"Акционерное общество \\\"Сбербанк Управление Активами\\\"\", \"7710183778\", \"44420370\", \"0252-74113866\", \"private_ppif\", \"stock_ppif\", \"TQIF\", \"TQIF\"],\n\t\t[94622, \"RU000A0JVRF4\", \"СберЖлНед3\", \"3030\", \"ПИФСбербанкЖилаяНедвижимость3\", \"RU000A0JVRF4\", 1, 1814, \"Акционерное общество \\\"Сбербанк Управление Активами\\\"\", \"7710183778\", \"44420370\", \"3030\", \"private_ppif\", \"stock_ppif\", \"TQIF\", \"TQIF\"],\n\t\t[108051, \"RU000A0JWAW3\", \"СберАрБизн\", \"3120\", \"ПИФ Сбербанк - Арендный бизнес\", \"RU000A0JWAW3\", 1, 1814, \"Акционерное общество \\\"Сбербанк Управление Активами\\\"\", \"7710183778\", \"44420370\", \"3120\", \"private_ppif\", \"stock_ppif\", \"TQIF\", \"TQIF\"],\n\t\t[137196, \"RU000A0ZYC64\", \"СберАрБиз2\", \"3219\", \"ПИФ Сбербанк-Арендный бизнес 2\", \"RU000A0ZYC64\", 1, 1814, \"Акционерное общество \\\"Сбербанк Управление Активами\\\"\", \"7710183778\", \"44420370\", \"3219\", \"private_ppif\", \"stock_ppif\", \"TQIF\", \"TQIF\"]\n\t]\n}}"
  val jObj = parse(json) \ "securities"
  case class Data(id:Int,
                  secid:String,
                  shortname:String,
                  regnumber:String,
                  name:String,
                  isin:String,
                  is_traded:Int,
                  emitend_id:Int,
                  emitent_title:String,
                  emitent_inn:String,
                  emitent_okpo:String,
                  gosreg:String,
                  _type:String,
                  group:String,
                  primary_boardid:String,
                  marketprice_boardid:String)

  val columns = jObj \ "columns"
  val data = jObj \ "data"


  def main(args: Array[String]):Unit= {
    val col = columns.extract[List[String]]
    val _data = data.extract[List[List[String]]]
    var storage = new ListBuffer[Data]()
//    for(d <- data){
//      print(d)
//    }
    for( d <- _data){
      storage += Data(d(0).toInt,d(1),d(2),d(3),d(4),d(5),d(6).toInt,d(7).toInt,d(8),d(9),d(10),d(11),d(12),d(13),d(14),d(15))
    }
    for(d<-storage){
      println(d)
    }

  }

}
