package section5

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, ChildRestartStats, Props}
import akka.testkit.{CallingThreadDispatcher, EventFilter, ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._

/**
 *  Learning points
 *
 *  1) Stopping a parent actor will automatically stop all of its child actor as well. The child actor will be killed before the parent actor terminated
 */

object scala_24_05_Stop_ParentActor_stop_childActor extends App {

  implicit val system = ActorSystem("SynchronousTestingSpec")

  object Parent {
    case class StartChild(name: String)
    case class StopChild(name: String)
    case object Stop
  }

  class Parent extends Actor with ActorLogging  {
    import Parent._
    override def receive: Receive = withChildren(Map())

    def withChildren(childrenMap: Map[String, ActorRef]) : Receive = {
      case StartChild(name) =>
        log.info(s"Starting child $name")
        context.become(withChildren(childrenMap + (name -> context.actorOf(Props[Child],name))))
      case Stop =>
        log.info("Stopping myself")
        context.stop(self)
      case message => log.info(message.toString)
    }
  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  import Parent._
  val parent = system.actorOf(Props[Parent], "parent")
  parent ! StartChild("child2")
  val child2 = system.actorSelection("/user/parent/child2")
  child2 ! "hi, second child"

  parent ! Stop
  parent ! "Parent are you still alive?"
  Thread.sleep(500)
  for (i <- 1 to 100) child2 ! s"[$i], second child, are you still alive?"

}

