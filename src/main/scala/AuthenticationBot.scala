import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.api.declarative._
import info.mukel.telegrambot4s.models.{Message, User}

/**
  * Extension to add a simple authentication filter.
  */


class AuthenticationBot() extends TelegramBot with Polling with Commands with SillyAuthentication {

  override def token: String = "598435632:AAEkD_zpBTtOFLZtoN7lMM9uP3umArhZvnY"


  onCommand("/login") { implicit msg =>
    for (user <- msg.from) {
      login(user)
      if (MyStockBot.important_shares.get(user.id).isEmpty) {
        MyStockBot.important_shares(user.id) = scala.collection.mutable.Set()
      }
    }
    reply("Now you have access")
  }

  onCommand("/logout") { implicit msg =>
    for (user <- msg.from)
      logout(user)
    reply("Bye bye!")
  }

}