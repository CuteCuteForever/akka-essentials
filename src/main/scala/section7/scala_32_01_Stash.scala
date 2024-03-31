package section7

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props, Stash}
import section6.scala_31_03_Mailboxes_deployment_config.{SimpleActor, system}

import scala.concurrent.duration._

object scala_32_01_Stash extends App {

  /**
   * ResourceActor
   * - open => it can receive read/write requests to the resource
   * - otherwise it will postpne all read/write requests until state is open
   *
   * ResourceActor is closed
   * - Open => switch to the open stage
   * - Read => messages are stash
   *   Write => messages are POSTPONED
   *
   * ResourceActor is open
   * - Read, Write are handled
   * - Close => switch to closed stage
   *
   */
  case object Open
  case object Close
  case object Read
  case class Write(data: String)

  class ResourceActor extends Actor with ActorLogging with Stash { //Step 1 add in Stash trait. Remember to mix in at the last part

    private var innerData: String = ""

    override def receive: Receive = closed

    def closed: Receive = {
      case Open => {
        log.info("Opening resource")
        unstashAll() //Step 3 - unstash all when you ready to process the message
        context.become(open)
      }
      case message =>
        log.info(s"Stashing $message as i cannot handle in the close state")
        stash() //Step 2 - stash away what you cant handle
    }

    def open: Receive = {
      case Read => log.info(s"I read the message - $innerData")
      case Write(data) => {
        log.info(s"I am writing $data")
        innerData = data
      }
      case Close => {
        log.info("Closing resource")
        unstashAll()
        context.become(closed)
      }
      case message =>
        log.info(s"Stashing $message as i cannot handle in the open state")
        stash()
    }
  }

  val system = ActorSystem("StashDemo")
  val resourceActor = system.actorOf(Props[ResourceActor], "ResourceActor")

  resourceActor ! Read //stashed
  resourceActor ! Open // switch to open; i have read ""
  resourceActor ! Open // stashed
  resourceActor ! Write("i love stash") //I am writing I love stash
  resourceActor ! Close //switch to closed state ; switch to Open state as Stash contains "Open"
  resourceActor ! Read // I have read I love stash

}

