package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

/**
 * The future usage inside Actor is highly discourage. If you running a future with a
 * long blocking call example "Thread.sleep(5000)" in our scenario,
 * you may starve the context dispatcher of running threads
 *
 * Types of Dispatcher :
 * 1) PinnedDispatcher - bind each actor to a thread pool of exactly 1 thread and those thread might circle around
 * 2) CallThreadDispatcher - ensure all invocation and communications with actor happen on calling thread
 * 3) Dispatcher(default)
 */
object scala_30_03_Dispatcher_future extends App {

  class DbActor extends Actor with ActorLogging {
    implicit val executionContext: ExecutionContext = context.dispatcher
    override def receive: Receive = {
      case message => Future {
        Thread.sleep(5000)
        log.info(s"[BlockingActor] : $message")
      } //(implicit contextExecution needed here
    }
  }

  class NonBlockingActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(s"[NonBlockingActor] $message")
    }
  }

  val system = ActorSystem("Demo")
  val dbActor = system.actorOf(Props[DbActor])
  val nonBlockingdbActor = system.actorOf(Props[NonBlockingActor])

  //send 1000 message to both actor
  for ( i <- 1 to 1000) {
    val message = s"message $i"
    dbActor ! message
    nonBlockingdbActor ! message
  }
}

