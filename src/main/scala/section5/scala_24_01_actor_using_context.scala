package section5

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props}

/**
 *  Stop actor using context.stop(ActorRef)
 */

object scala_24_01_actor_using_context extends App {

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
      case StopChild(name) =>
        log.info(s"Stopping child wiht the name $name")
        val childOption = childrenMap.get(name)
        childOption.foreach( childRef => context.stop(childRef)) //this is how you stop a actor asynchronously
    }
  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  import Parent._
  val parent = system.actorOf(Props[Parent], "parent")
  parent ! StartChild("child1")
  val child = system.actorSelection("/user/parent/child1")
  child ! "hi kid!"
  parent ! StopChild("child1")


}

