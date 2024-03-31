package section3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

//Learing points - Using "context.become(anotherPartialFunction)" to change the message handler
//From Stateful actor to stateless actor using msg handler
object scala_12_01_ContextBecome extends App {

  object FussyKid{
    case object KidAccept
    case object KidReject
    val HAPPY = "happy"
    val SAD = "sad"
  }

  //Below this code is not good as it is having a var mutable
  class FussyKid extends Actor {
    import FussyKid._
    import Mom._
    var state = HAPPY
    override def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(_) => if (state==HAPPY) sender() ! KidAccept else sender() ! KidReject
    }
  }

  class StatelessFussyKid extends Actor {
    import FussyKid._
    import Mom._

    override def receive: Receive = happyReceive

    def happyReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive) //using this trick to change the message handler
      case Food(CHOCOLATE) =>
      case Ask(_) => sender() ! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLE) =>
      case Food(CHOCOLATE) => context.become(happyReceive)
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
    import Mom._
    import FussyKid._
    override def receive: Receive = {
      case MomStart(kidRef) => {
        kidRef ! Food(VEGETABLE)
        kidRef ! Ask("do you want to play?")
      }
      case KidAccept => println("Yay, my kid is happy")
      case KidReject => println("My kid is sad but he is healthy")
    }
  }

  val system = ActorSystem("changingActorBehaviourDemo")
  val fussyKid = system.actorOf(Props[FussyKid])
  val mom = system.actorOf(Props[Mom])
  val statelessFussyKid = system.actorOf(Props[StatelessFussyKid])

  import Mom._
  /*
    mom receives MomStart
      kid receives Food(veg) -> Kid will change msgHandler to sadReceive
      kid received Ask(play?) -> Kid replies with sadReceive handler => "sender() ! KidReject"
    mom received KidReject
   */
  mom ! MomStart(statelessFussyKid)
}