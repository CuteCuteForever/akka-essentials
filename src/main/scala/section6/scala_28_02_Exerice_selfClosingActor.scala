package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props}
import section6.scala_28_02_Exerice_selfClosingActor.SelfClosingActor

import scala.concurrent.duration._

/**
 * 1) Actor receive a message, actor is alive for next 2 seconds
 *    - If no message is received then after 2 seconds, actor will self terminate itself
 *    - if another message is received, actor is alive for next 2 seconds again
 */
object scala_28_02_Exerice_selfClosingActor extends App {

  val system=ActorSystem("SchedulersTimersDemo")
  case object ReadFile
  import system.dispatcher

  class SelfClosingActor extends Actor with ActorLogging  {

    var schedule = createTimeoutWindow()

    override def receive: Receive = {
      case "SelfStop" =>
        log.info("Stopping myself")
        context.stop(self)
      case message =>
        log.info(s"Received $message, staying alive")
        schedule.cancel()
        schedule = createTimeoutWindow() //a function to send a message to ownself to kick start to stop after 2 seconds
    }

    def createTimeoutWindow(): Cancellable = {
      context.system.scheduler.scheduleOnce(2 second) {
        self ! "SelfStop"
      }
    }
  }

  val selfClosingActor = system.actorOf(Props[SelfClosingActor], "selfClosingActor")

  system.scheduler.scheduleOnce(1 seconds) {
    selfClosingActor ! "ping"
  }

  system.scheduler.scheduleOnce(5 seconds){
    system.log.info("sending ping again to self-closing actor after 5 seconds")
    selfClosingActor ! "ping"
  }

}

