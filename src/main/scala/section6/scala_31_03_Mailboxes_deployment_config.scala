package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.dispatch.ControlMessage
import com.typesafe.config.ConfigFactory

/**
 * Create a mailbox having priority queue using configurations
 */
object scala_31_03_Mailboxes_deployment_config extends App {

  val system = ActorSystem("MailBoxesDemo", ConfigFactory.load().getConfig("mailboxesDemoConfig"))

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  //Step 1 - mark important messages as control messages
  case object ManagementTicket extends ControlMessage

  val altControlAwareActor = system.actorOf(Props[SimpleActor], "altControlAwareActor")

  altControlAwareActor ! "[P3] this thing would be nice to have"
  altControlAwareActor ! "[P0] this needs to be solved NOW"
  altControlAwareActor ! ManagementTicket

  
}