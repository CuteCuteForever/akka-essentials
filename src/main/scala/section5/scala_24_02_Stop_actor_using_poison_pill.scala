package section5

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props}

/**
 *  Stop actor using poison pill
 */
object scala_24_02_Stop_actor_using_poison_pill extends App {

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

  val looseActor = system.actorOf(Props[Child])
  looseActor ! "Hello, loose Actor"
  looseActor ! PoisonPill //this is inbuilt messages to kill an actor
  looseActor ! "Hello, loose Actor"

}

