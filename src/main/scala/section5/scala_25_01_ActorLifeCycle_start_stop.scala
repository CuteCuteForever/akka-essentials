package section5

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props}

/**
 *  Start will run preStart()
 *  Stop will run postStop()
 */
object scala_25_01_ActorLifeCycle_start_stop extends App {

  object StartChild

  class LifeCycleActor extends Actor with ActorLogging {

    override def preStart(): Unit = log.info("I am starting")
    override def postStop(): Unit = log.info("I am stopping")

    override def receive: Receive = {
      case StartChild =>
        context.actorOf(Props[LifeCycleActor], "child")
    }
  }

  val system = ActorSystem("LifeCycleDemo")
  val parent = system.actorOf(Props[LifeCycleActor], "parent")
  parent ! StartChild
  Thread.sleep(500)
  println("=============================================")
  parent ! PoisonPill
}

