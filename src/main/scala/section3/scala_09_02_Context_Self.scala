package section3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object scala_09_02_Context_Self extends App {

  val system = ActorSystem("actorCapabilitiesDemo")

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message : String => println(s"[${context.self}] i have received $message")
      case SendMessageToYourself(contents) => self ! contents //context.self is equivalent to self as well
    }
  }
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

  // 2. Actors have information about their context and about themselves
  // context.self / self === 'this' in OOP
  // You can send message to yourself
  case class SendMessageToYourself(context: String)
  simpleActor ! SendMessageToYourself("I am an actor and i am proud of it")
}
