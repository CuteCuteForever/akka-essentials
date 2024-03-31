package section3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object scala_09_05_ForwardingMessages extends App {

  val system = ActorSystem("actorCapabilitiesDemo")

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case WirelessPhoneMessage(content: String, ref: ActorRef) => ref forward (content + "s")
      case message: String => println(s"${self.path} I have received $message")
    }
  }

  // 5 - forwarding message using "forward"
  case class WirelessPhoneMessage(content: String, ref: ActorRef)
  val alice = system.actorOf(Props[SimpleActor], "Alice")
  val bob = system.actorOf(Props[SimpleActor], "Bob")

  //No Sender -> Alice -> Bob
  alice ! WirelessPhoneMessage("Hi", bob)

}