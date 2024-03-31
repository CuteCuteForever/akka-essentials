package section3

import akka.actor.{Actor, ActorSystem, Props}

/* Learning points -
   1. Using "context.become(anotherPartialFunction, boolean)" to push the current msg handler into stack if "false"
   2. Use "context.unbecome()" to pop the msg handler stack

   Note that the boolean indicate :
     true - discard previous old message handler
     false- add the previous message handler to the msg handler stack

   Note that Akka always uses the latest handler on top of the stack
   */
object scala_14_01_Exercise_Counter extends App {

  import CounterActor._
  val system = ActorSystem("actorSystem")

  object CounterActor {
    case object Increment
    case object Decrement
    case object Print
  }

  class CounterActor extends Actor {

    override def receive: Receive = counterOperation(0)

    def counterOperation(value: Int) : Receive = {
      case Increment => context.become(counterOperation(value+1))
      case Decrement => context.become(counterOperation(value-1))
      case Print => println(s"Counter is $value")
    }
  }

  val counterActor = system.actorOf(Props[CounterActor], "CounterActor")

  counterActor ! Increment
  counterActor ! Print
  counterActor ! Decrement
  counterActor ! Print


}