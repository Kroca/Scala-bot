import java.nio.file.Paths

import info.mukel.telegrambot4s.models.{InputFile, User}

import collection.mutable.Stack
import org.scalatest._

class BotTest extends FlatSpec {

  "A MyStockBot" should "generate candles picture" in {
    // Initialize user and authenticator
    MyStockBot.createGraph("SBER")
    val photo = Paths.get("chart.png")
    assert(photo.toFile().getTotalSpace() > 0)
  }
}