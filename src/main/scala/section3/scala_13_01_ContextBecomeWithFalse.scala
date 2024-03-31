package section3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/* Learning points -
   1. Using "context.become(anotherPartialFunction, boolean)" to push the current msg handler into stack if "false"
   2. Use "context.unbecome()" to pop the msg handler stack

   Note that the boolean indicate :
     true - discard previous old message handler
     false- add the previous message handler to the msg handler stack

   Note that Akka always uses the latest handler on top of the stack
   */
object scala_13_01_ContextBecomeWithFalse extends App {

  object FussyKid{
    case object KidAccept
    case object KidReject
    val HAPPY = "happy"
    val SAD = "sad"
  }

  class StatelessFussyKid extends Actor {
    import FussyKid._
    import Mom._

    override def receive: Receive = happyReceive

    def happyReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false) // false means stack
      case Food(CHOCOLATE) =>
      case Ask(_) => sender() ! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false) // false means stack
      case Food(CHOCOLATE) => context.unbecome()
      case Ask(_) => sender() ! KidReject
    }
  }

  object Mom {
    case class MomStart(kidRef : ActorRef)
    case class Food(food:String)
    case class Ask(message: String)
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }
  class Mom extends Actor {
    import FussyKid._
    import Mom._
    override def receive: Receive = {
      case MomStart(kidRef) => {
        kidRef ! Food(VEGETABLE)
        kidRef ! Food(VEGETABLE)
        kidRef ! Food(CHOCOLATE)
        kidRef ! Food(CHOCOLATE)
        kidRef ! Ask("do you want to play?")
      }
      case KidAccept => println("Yay, my kid is happy")
      case KidReject => println("My kid is sad but he is healthy")
    }
  }

  val system = ActorSystem("changingActorBehaviourDemo")
  val mom = system.actorOf(Props[Mom])
  val statelessFussyKid = system.actorOf(Props[StatelessFussyKid])

  import Mom._
  /*
      if kid receives Food(veg) -> Kid will change msgHandler to sadReceive(false)
        Stack:
        1. sadReceive (add in because of "false")
        2. happyReceive
      kid receives Food(chocolate) -> Kid will change msgHandler to happyReceive(false)
        Stack:
        1. happyReceive (add in because of "false")
        2. sadReceive
        3. happyReceive
   */
  mom ! MomStart(statelessFussyKid)
}