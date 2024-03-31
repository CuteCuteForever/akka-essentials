package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.dispatch.{ControlMessage, PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.{Config, ConfigFactory}
import section6.scala_31_01_Mailboxes_priorityQueue.{SimpleActor, system}

/**
 * Create a mailbox having priority queue
 */
object scala_31_02_Mailboxes_controlAware extends App {

  val system = ActorSystem("MailBoxesDemo", ConfigFactory.load().getConfig("mailboxesDemo"))

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  //Step 1 - mark important messages as control messages
  case object ManagementTicket extends ControlMessage

  //step 2 - configure who gets the mailbox
  // - make actor attach to mailbox
  val controlAwareActor = system.actorOf(Props[SimpleActor].withMailbox("control-mailbox"))

  controlAwareActor ! "[P3] this thing would be nice to have"
  controlAwareActor ! "[P0] this needs to be solved NOW"
  controlAwareActor ! ManagementTicket


}