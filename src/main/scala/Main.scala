import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.JsonMethods._

import org.json4s.JsonDSL._
import scala.reflect.internal.util.TableDef.Column

object Main {

  def main(args: Array[String]):Unit= {

    MyStockBot.run()
  }

}
