package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.util.Random

object scala_30_01_Dispatcher_usingConfig extends App {

  class CounterActor extends Actor with ActorLogging {

    var count =0;

    override def receive: Receive = {
      case message =>
        count+= 1
        log.info(s"[$count] message is $message")
    }
  }

  val system = ActorSystem("DispatcherDemo", ConfigFactory.load().getConfig("dispatchersDemo"))

  //attaching a dispatcher to a actor using config. However, not sure how to test of this.
  val actors = for (i <- 1 to 10) yield system.actorOf(Props[CounterActor], "rtjvm")
}