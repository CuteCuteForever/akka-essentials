package section6

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, OneForOneStrategy, Props}
import akka.pattern.{Backoff, BackoffSupervisor}

import java.io.File
import scala.concurrent.duration._
import scala.io.Source

object scala_28_01_TimersSchedulers extends App {

  val system=ActorSystem("SchedulersTimersDemo")
  case object ReadFile

  class SimpleActor extends Actor with ActorLogging  {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val simpleActor = system.actorOf(Props[SimpleActor])

  system.log.info("Scheduling reminder for simpleActor")

  /**
   * How to write codes to ensure it run 2 seconds later
   */
  import system.dispatcher

  system.scheduler.scheduleOnce(2 seconds){
    simpleActor ! "reminder"
  }// (system.dispatcher) <- implicit parameters here. System.dispatcher implement the execution context

  val scheduler: Cancellable = system.scheduler.schedule(1 seconds, 2 seconds){
    simpleActor ! "heartbeat"
  }

  system.scheduler.scheduleOnce(5 seconds){
    scheduler.cancel()
  }
}

