import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.api.declarative._
import info.mukel.telegrambot4s.models.{Message, User}

/**
  * Extension to add a simple authentication filter.
  */


class AuthenticationBot() extends TelegramBot with Polling with Commands with SillyAuthentication {

  override def token: String = "598435632:AAEkD_zpBTtOFLZtoN7lMM9uP3umArhZvnY"


}