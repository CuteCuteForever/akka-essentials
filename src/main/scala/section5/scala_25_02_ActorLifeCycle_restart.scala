package section5

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}

/**
 *
 * If actor threw an exception while processing a message,
 * this message causing the exception will be removed from queue and not put in mailbox. Actor is restarted
 *
 * Restart will run preRestart().
 * Then the actor instance will be replace in the actor reference. All state lost
 * Then the new actor instance will run postRestart()
 *
 */
object scala_25_02_ActorLifeCycle_restart extends App {

  object Fail
  object FailChild
  object CheckChild
  object Check

  class Parent extends Actor {
    private val child = context.actorOf(Props[Child], "supervisedChild")

    override def receive: Receive = {
      case FailChild => child ! Fail
      case CheckChild => child ! Check
    }
  }

  class Child extends Actor with ActorLogging {

    override def preStart(): Unit = log.info("Child started")
    override def postStop(): Unit = log.info("Child stopped")
    override def preRestart(reason: Throwable, message: Option[Any]): Unit =
      log.info(s"Child Actor preRestart() to ask for a replacement on Actor Instance")
    override def postRestart(reason: Throwable): Unit =
      log.info(s"Child Actor postRestart()")

    override def receive: Receive = {
      case Fail =>
        log.warning("child will fail now")
        throw new RuntimeException("RunTimeException")
      case Check =>
        log.info("alive and kicking")
    }
  }

  val system = ActorSystem("LifeCycleDemo")
  val parent = system.actorOf(Props[Parent], "parent")
  parent ! FailChild
  parent ! CheckChild
}

