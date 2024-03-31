package section3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object scala_09_06_Exercise_Counter extends App {

  val system = ActorSystem("actorSystem")

  class CounterActor extends Actor {

    var counter: Int = 0;

    override def receive: Receive = {
      case Increment(value: Int) => counter += value
      case Decrement(value: Int) => counter -= value
      case Print => println(s"Counter is $counter")
    }
  }

  case class Increment(value: Int)
  case class Decrement(value: Int)
  case object Print

  val counterActor = system.actorOf(Props[CounterActor], "CounterActor")

  counterActor ! Increment(1)
  counterActor ! Print
  counterActor ! Decrement(2)
  counterActor ! Print

}