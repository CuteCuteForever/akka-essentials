package section7

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{ExecutionContext}
import scala.util.{Failure, Success}

class scala_33_01_AskSpec extends TestKit(ActorSystem("AskSpec")) with ImplicitSender with WordSpecLike with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import scala_33_01_AskSpec._

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

object scala_33_01_AskSpec {

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
      val originalSender = sender()

      /**
       * Step 3 - Use the "?" Ask
       */
      val future = kvActor ? Read(username) //(implicit operations)

      /**
       * Step 4 - handle the future for e.g. with onComplete
       */
      future.onComplete {
        case Success(None) => originalSender ! AuthFailure(AUTH_FAILURE_NOT_FOUND)
        case Success(Some(dbPassword)) =>
          if (dbPassword == password) originalSender ! AuthSuccess
          else originalSender ! AuthFailure(AUTH_FAILURE_PASSWORD_INCORRECT)
        case Failure(_) => originalSender ! AuthFailure(AUTH_FAILURE_SYSTEM)
      }
    }
  }
}




