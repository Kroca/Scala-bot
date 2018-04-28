import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.api.declarative._
import info.mukel.telegrambot4s.models.{Message, User}

/**
  * Extension to add a simple authentication filter.
  */


class AuthenticationBot() extends TelegramBot with Polling with Commands with SillyAuthentication {

  override def token: String = "586545759:AAFLyEQwU93zQ38t2aZflaB4F33IGdERtQk"


}