package section7

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 * Step 1 - Import
 */
import akka.pattern.{ask, pipe}

class scala_33_02_AskSpe_pipe extends TestKit(ActorSystem("AskSpec")) with ImplicitSender with WordSpecLike with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import scala_33_02_AskSpe_pipe._

  "An authenticator" should {
    "fail to authenticate a non-registered user" in {
      val authManager = system.actorOf(Props[AuthManager])
      authManager ! Authenticate("daniel", "rtjvm")
      expectMsg(AuthFailure(AuthManager.AUTH_FAILURE_NOT_FOUND))
    }

    "fail to authenticate if invalid password" in {
      val authManager = system.actorOf(Props[AuthManager])
      authManager ! RegisterUser("daniel", "rtjvm")
      authManager ! Authenticate("daniel", "12345")
      expectMsg(AuthFailure(AuthManager.AUTH_FAILURE_PASSWORD_INCORRECT))
    }
  }

}

object scala_33_02_AskSpe_pipe {

  case class Read(key: String)
  case class Write(key: String, value: String)

  class KVActor extends Actor with ActorLogging{
    override def receive: Receive = online(Map())
    def online(kv:Map[String, String]): Receive = {
      case Read(key) =>
        log.info(s"Trying to read the value at the key $key")
        sender() ! kv.get(key) //return Option[String]
      case Write(key, value) =>
        log.info(s"Writing the value $value for the key $key")
        context.become(online(kv + (key -> value)))
    }
  }

  case class RegisterUser(username: String , password: String)
  case class Authenticate(username: String, password: String)
  case class AuthFailure(message:String)
  case object AuthSuccess
  object AuthManager {
    val AUTH_FAILURE_NOT_FOUND = "username not found"
    val AUTH_FAILURE_PASSWORD_INCORRECT = "password incorrect"
    val AUTH_FAILURE_SYSTEM = "system error"
  }
  class AuthManager extends Actor with ActorLogging {

    import AuthManager._

    /**
     * Step 2 - Need implicit for below operation
     */
    implicit  val timeout: Timeout = Timeout(1 seconds)
    implicit val executionContext: ExecutionContext = context.dispatcher

    protected val kvActor = context.actorOf(Props[KVActor])

    override def receive: Receive = {
      case RegisterUser(username, password) => kvActor ! Write(username, password)
      case Authenticate(username, password) => handleAuthentication(username, password)
    }


    def handleAuthentication(username: String, password: String) = {
      /**
       * Step 3 - Using "?" Ask operation
       */
      val future = kvActor ? Read(username) //(Implicit operations) Future[Any]
      val passwordFuture = future.mapTo[Option[String]] //Future[Option[String]]
      /**
       * Step 4 - handle the future for e.g. with onComplete
       */
      val responseFuture = passwordFuture.map {
        case None => AuthFailure(AUTH_FAILURE_NOT_FOUND)
        case Some(dbPassword) =>
          if (dbPassword == password) AuthSuccess
          else AuthFailure(AUTH_FAILURE_PASSWORD_INCORRECT)
      } //Future[AuthSucess/AuthFailure]

      /**
       * Step 5 - pipe the future result back to sender()
       */
      responseFuture.pipeTo(sender()) //Using Pipe to send back to sender() the response future value
    }
  }
}




