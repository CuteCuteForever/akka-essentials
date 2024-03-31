package section3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object scala_09_03_ReplyingMessageToSender extends App {

  val system = ActorSystem("actorCapabilitiesDemo")

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case SayHiTo(ref) => ref ! "Hi" // Note that there is implicit value "self" E.g. (ref ! "Hi)(self). This self reference become a context.sender
      case "Hi" => sender() ! "Hello There!" //context.sender refer to last sent message
      case msg: String => println(s"I have received $msg")
    }
  }
  val alice = system.actorOf(Props[SimpleActor], "Alice")
  val bob = system.actorOf(Props[SimpleActor], "Bob")

  // 3 - actors can send message to another actor
  case class SayHiTo(ref: ActorRef) //note that ActorRef refer to Actor instances
  alice ! SayHiTo(bob) //NoSender -> Alice ("Hi") -> Bob ("Hello There") -> Alice

}
