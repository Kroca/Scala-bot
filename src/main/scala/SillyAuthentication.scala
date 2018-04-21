import info.mukel.telegrambot4s.api.declarative.Action
import info.mukel.telegrambot4s.models.{Message, User}

trait SillyAuthentication {
  val allowed = scala.collection.mutable.Set[Int]()

  def login(user: User) = atomic {
    allowed += user.id
  }

  def atomic[T](f: => T): T = allowed.synchronized {
    f
  }

  def logout(user: User) = atomic {
    allowed -= user.id
  }

  def authenticatedOrElse(ok: Action[User])(noAccess: Action[User])(implicit msg: Message): Unit = {
    msg.from.foreach {
      user =>
        if (isAuthenticated(user))
          ok(user)
        else
          noAccess(user)
    }
  }

  def isAuthenticated(user: User): Boolean = atomic {
    allowed.contains(user.id)
  }

  def authenticated(ok: Action[User])(implicit msg: Message): Unit = {
    msg.from.foreach {
      user =>
        if (isAuthenticated(user))
          ok(user)
    }
  }
}