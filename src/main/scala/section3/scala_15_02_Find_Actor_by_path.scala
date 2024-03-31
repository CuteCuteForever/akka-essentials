package section3

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}

/* Learning Points
  Guardian actors (top-level)
  - /system = system guardian (manage environment)
  - /user = user-level guardian (manage the actor that was created by developers via of system.actorOf)
  - / = root guardian (manage both system and user )
 */
object scala_15_02_Find_Actor_by_path extends App {

  val system = ActorSystem("actorSystem")

  object Parent {
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }

  class Parent extends Actor {
    import Parent._

    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"${self.path} creating child")
        val childRef = context.actorOf(Props[Child], name)//create new actor right HERE using context.actorOf instead of system.actorOf
        context.become(withChild(childRef))
    }

    def withChild(childRef: ActorRef): Receive = {
      case TellChild(message) => childRef forward message
    }
  }

  class Child extends Actor {
    override def receive: Receive = {
      case message => println(s"${self.path} I got: $message")
    }
  }

  import Parent._
  val parentActor = system.actorOf(Props[Parent],"parent")

  parentActor ! CreateChild("child")

  //Find actor by path
  val childSelection: ActorSelection = system.actorSelection("user/parent/child") //ActorSelection - wrapper of ActorRef
  childSelection ! "I found you "

}