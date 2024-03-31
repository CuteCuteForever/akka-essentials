package section3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import javax.lang.model.util.SimpleElementVisitor6

/*
 *
 *
 * Actor have a context object store in it that contains lots of information such as Self, etc..
 */
object scala_09_01_ActorsMessagesBehaviours extends App {

  val system = ActorSystem("actorCapabilitiesDemo")

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case SayHiTo(ref) => ref ! "Hi"
      case "Hi" => context.sender() ! "Hello There!" //context.sender refer to last sent message

      case message : String => println(s"[${context.self}] i have received $message")
      case number: Int => println(s"[${context.self.path}] I have received a NUMBER: $number")
      case SpecialMessage(contents) => println(s"[simple actor] I have received a special message: $contents")
      case SendMessageToYourself(contents) => self ! contents //context.self is equivalent to self as well
    }
  }

  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")
  //simpleActor ! "hello, actor"
  //simpleActor ! 42

   //1 - Send any type of messages - In normal, case classes/objects which is often used
   // messages must be IMMUTABLE
   // messages must be SERIALIZABLE
   // In normals, always use case classes and case objects

  case class SpecialMessage(contents: String)
  //simpleActor ! SpecialMessage("some special content")

  // 2. Actors have information about their context and about themselves
  // context.self / self === 'this' in OOP
  // You can send message to yourself
  case class SendMessageToYourself(context: String)
  //simpleActor ! SendMessageToYourself("I am an actor and i am proud of it")

  //3. actors can reply to message
  val alice = system.actorOf(Props[SimpleActor], "Alice")
  val bob = system.actorOf(Props[SimpleActor], "Bob")

  case class SayHiTo(ref: ActorRef) //note that ActorRef refer to Actor instances
  alice ! SayHiTo(bob)

  //4 - dead letters
  alice ! "hi"
}
