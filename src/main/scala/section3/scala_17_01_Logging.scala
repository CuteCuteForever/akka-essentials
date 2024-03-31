package section3

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.event.Logging

object scala_17_01_Logging extends App {

  val system = ActorSystem("actorSystem")

  // #1 - explicit logging
  class SimpleActorWithExplicitLogger extends Actor {
    val logger = Logging(context.system, this)
    override def receive: Receive = {
          /*
            1 - DEBUG
            2 - INFO
            3 - WARN
            4 - ERROR
           */
      case message => logger.info(message.toString)
    }
  }
  val simpleActorWithExplicitLogger = system.actorOf(Props[SimpleActorWithExplicitLogger], "simpleActorWithExplicitLogger")
  simpleActorWithExplicitLogger ! "Logging a simple message"

  // #2 - ActorLogging traits
  class ActorWithLogging extends Actor with ActorLogging {
    override def receive: Receive = {
      case (a,b) => log.info("Two things: {} and {}",a,b)
      case message => log.info(message.toString)
    }
  }

  val actorWithLogging = system.actorOf(Props[ActorWithLogging], "actorWithLogging")
  actorWithLogging ! "Logging a simple message by extending a trait"
  actorWithLogging ! (42,65)


}