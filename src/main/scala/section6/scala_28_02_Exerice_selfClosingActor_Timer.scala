package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props, Timers}

import scala.concurrent.duration._

/**
 * Timers is good to send message to self at periodic timing
 */
object scala_28_02_Exerice_selfClosingActor_Timer extends App {

  val system=ActorSystem("SchedulersTimersDemo")

  case object TimerKey
  case object Start //TimerKey can only be case object
  case object Stop //TimerKey can only be case object
  case object Reminder

  class TimerBasedActor extends Actor with ActorLogging with Timers  {

    timers.startSingleTimer(TimerKey, Start, 500 millis) //message to send myself is Start

    override def receive: Receive = {
      case Start =>
        log.info("Bootstrapping and start Periodic Timer")
        timers.startPeriodicTimer(TimerKey, Reminder, 1 second) //if i use the same key (Timerkey), the previous timer associate with this key is being cancelled
      case Reminder =>
        log.info("I am alive")
      case Stop =>
        log.warning("Stopping!")
        timers.cancel(TimerKey)
        context.stop(self)
    }
  }

  import system.dispatcher

  val timerBasedActor = system.actorOf(Props[TimerBasedActor], "timerBasedActor")
  system.scheduler.scheduleOnce(5 seconds) {
    timerBasedActor ! Stop
  }


}

