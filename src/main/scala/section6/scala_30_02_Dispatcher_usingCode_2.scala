package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.util.Random

object scala_30_02_Dispatcher_usingCode_2 extends App {

  class Counter extends Actor with ActorLogging {

    var count =0;

    override def receive: Receive = {
      case message =>
        count+= 1
        log.info(s"[$count] message is $message")
    }
  }

  val system = ActorSystem("DispatcherDemo") //, ConfigFactory.load().getConfig("dispatchersDemo"))

  val actors = for ( i <- 1 to 10) yield system.actorOf(Props[Counter].withDispatcher("my-dispatcher"), s"counter_$i")

  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"
  actors(0) ! "message"
  actors(1) ! "message"
  actors(2) ! "message"
  actors(3) ! "message"
  actors(4) ! "message"
  actors(5) ! "message"
  actors(6) ! "message"
  actors(7) ! "message"
  actors(8) ! "message"
  actors(9) ! "message"

}




