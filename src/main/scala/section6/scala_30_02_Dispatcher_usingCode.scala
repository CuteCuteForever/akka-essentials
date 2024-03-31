package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import com.typesafe.config.ConfigFactory

import scala.util.Random

object scala_30_02_Dispatcher_usingCode extends App {

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

  val r = new Random()
  for (i <- 1 to 1000){
    /**
     * Even though actor selected for random but dispatcher need to
     * allocate thread for them in order for actor to work on the
     * messages
     */
    actors(r.nextInt(10)) ! i }
}


/*actors(0) ! "message"
actors(1) ! "message"
actors(2) ! "message"
actors(3) ! "message"
actors(4) ! "message"
actors(5) ! "message"
actors(6) ! "message"
actors(7) ! "message"
actors(8) ! "message"
actors(9) ! "message"*/

