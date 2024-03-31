package section5

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern.{Backoff, BackoffSupervisor}

import java.io.File
import scala.io.Source
import scala.concurrent.duration._

object scala_27_01_BackOffSupervisor_onFailure extends App {

  val system=ActorSystem("BackoffSupervisorDemo")
  case object ReadFile

  class FileBasedPersistentActor extends Actor with ActorLogging {
    var dataSource: Source = null

    override def preStart(): Unit = log.info("Actor Start")
    override def postStop(): Unit = log.info("Actor Stop")
    override def preRestart(reason: Throwable, message: Option[Any]): Unit = log.info("Actor restarting")

    override def receive: Receive = {
      case ReadFile =>
        if (dataSource == null) {
          dataSource = Source.fromFile(new File("src/main/resource/testfiles/important_data.txt"))
          log.info("I have just read some IMPORTANT data")
        }
    }
  }

  val simpleSupervisorProps = BackoffSupervisor.props( //default supervisor strategy is Restart if any exception found
    Backoff.onFailure(
      Props[FileBasedPersistentActor],
      "simpleChildBackoffActor", // a child call simpleChildBackoffActor is being created
      2 seconds, //first attempt is 2s then 4s, 8s, 16s etc..
      30 seconds, //max cap at 30 seconds
      0.2
    )
  )

  /**
   * Exception -> Supervisor -> BackOffSupervisor to handle the start operation on interval duration
   *
   * when exception occur, Supervisor strategy will try to Restart the stopChildBackOffActor
   * BackOffSupervisor kicks in and ensure that the stopChildBackOffActor try to start after 2 seconds
   *
   * When the next exception occur, Supervisor strategy will Restart the stopChildBackOffActor again
   * BackOffSupervisor kicks in and ensure that the stopChildBackOffActor try to start after 4 seconds
   */
  val simpleParentSupervisor = system.actorOf(simpleSupervisorProps, "simpleParentSupervisor")
  simpleParentSupervisor ! ReadFile
  Thread.sleep(3000) //wait for it to load for 2 seconds to complete as Actor will only be start after 2 seconds
  simpleParentSupervisor ! ReadFile //check the logs here . It will delay for 4 seconds on the next Actor that is starting

}

