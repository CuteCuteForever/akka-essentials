package section5

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Kill, Props}

/**
 *  Stop actor using kill
 */
object scala_24_03_Stop_actor_using_kill extends App {

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
        childOption.foreach( childRef => context.stop(childRef))
    }
  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val abruptlyTerminatedActor = system.actorOf(Props[Child])
  abruptlyTerminatedActor ! "You are about to be terminated"
  abruptlyTerminatedActor ! Kill // stop actor using Kill. Kill will throw akka.actor.ActorKilledException
  abruptlyTerminatedActor ! "Are you still alive?"
}

