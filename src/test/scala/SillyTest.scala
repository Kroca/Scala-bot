import info.mukel.telegrambot4s.models.User

import collection.mutable.Stack
import org.scalatest._

class SillyTest extends FlatSpec {

  "A SillyAuthenticator" should "login user" in {
    // Initialize user and authenticator
    val bot = SillyImpl()
    val user = User(1, false, "UserTest")
    // Check user is not auth
    assert(bot.isAuthenticated(user) === false)
    bot.login(user)
    // Check user is logged in
    assert(bot.isAuthenticated(user) === true)
  }

  it should "logout user" in {
    // Initialize user and authenticator
    val bot = SillyImpl()
    val user = User(1, false, "UserTest")
    // Check user is not auth
    assert(bot.isAuthenticated(user) === false)
    bot.login(user)
    // Check user is logged in
    assert(bot.isAuthenticated(user) === true)
    bot.logout(user)
    // Check user is logged out
    assert(bot.isAuthenticated(user) === false)
  }

  case class SillyImpl() extends SillyAuthentication

}