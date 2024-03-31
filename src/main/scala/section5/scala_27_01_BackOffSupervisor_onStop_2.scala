package section5

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props}
import akka.pattern.{Backoff, BackoffSupervisor}

import java.io.File
import scala.concurrent.duration._
import scala.io.Source

object scala_27_01_BackOffSupervisor_onStop_2 extends App {

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
    Backoff.onStop(
      Props[FileBasedPersistentActor],
      "stopChildBackoffActor", // a child call simpleChildBackoffActor is being created
      2 seconds, // this value * 2 for the next value. E.g first attempt is 3s then 6s, 12s, 24s etc...
      30 seconds, //max cap at 30 seconds
      0.2
    ).withSupervisorStrategy(
      OneForOneStrategy(){
        case _ => Stop
      }
    )
  )

  val stopParentSupervisor = system.actorOf(simpleSupervisorProps, "stopParentSupervisor")
  stopParentSupervisor ! ReadFile
  Thread.sleep(3000) //wait for it to load for 2 seconds initially
  stopParentSupervisor ! ReadFile //check the logs here . It will delay for 4 seconds

}

