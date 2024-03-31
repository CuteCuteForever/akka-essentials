package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.{Config, ConfigFactory}
import section6.scala_31_01_Mailboxes_priorityQueue.SimpleActor

/**
 * Create a mailbox having priority queue
 */
object scala_31_01_Mailboxes_priorityQueue extends App {

  val system = ActorSystem("MailBoxesDemo")

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  /**
   * Case 1 - custom priority mailbox priority queue
   * P0 -> most important , P1 , P2, P3 ...
   */
  //Step 1 - create a mailbox
  case class SupportTicketPriorityMailBox(settings: ActorSystem.Settings, config: Config) extends UnboundedPriorityMailbox(
    PriorityGenerator{ // Any => Number
      case message:String if message.startsWith("[P0]") => 0 //lower number means higher priority
      case message:String if message.startsWith("[P1]") => 1
      case message:String if message.startsWith("[P2]") => 2
      case message:String if message.startsWith("[P3]") => 3
      case _ => 4
    }
  )

  //step 2 - make it known to config
  //step 3 - attach dispatech to actor
  val supportTicketLogger = system.actorOf(Props[SimpleActor].withDispatcher("support-ticket-dispatcher"))

  supportTicketLogger ! "[P3] this thing would be nice to have"
  supportTicketLogger ! "[P0] this needs to be solved NOW"
  supportTicketLogger ! "[P1] do this when you have time"
  //after which time can i send another message and be prioritized accordingly? Ans: You cant do anything
}