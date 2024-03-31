package section5

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props}
import akka.pattern.{Backoff, BackoffSupervisor}

import java.io.File
import scala.concurrent.duration._
import scala.io.Source

object scala_27_01_BackOffSupervisor_onStop extends App {

  val system=ActorSystem("BackoffSupervisorDemo")
  case object ReadFile

  class FileBasedPersistentActor extends Actor with ActorLogging {
    var dataSource: Source = null

    override def preStart(): Unit = {
      log.info("Actor Start")
      dataSource = Source.fromFile(new File("src/main/resource/testfiles/important_data.txt")) // will throw ActorInitializationException here which leadd to a Stop in directive
    }
    override def postStop(): Unit = log.info("Actor Stop")
    override def preRestart(reason: Throwable, message: Option[Any]): Unit = log.info("Actor restarting")

    override def receive: Receive = {
      case ReadFile => ???
    }
  }

  /**
   *  1. FileBasedPersistentActor init but throw ActorInitializationException error as preStart() unable locate resource file
   *  2. Trigger the supervision strategy which leads to STOP directive
   *  3. BackoffSupervisor kicks in which will continue the process again every 1s, 2s, 4s, 8, etc...
   */
  val simpleSupervisorProps = BackoffSupervisor.props( //default supervisor strategy is Restart if any exception found
    Backoff.onStop(
      Props[FileBasedPersistentActor],
      "stopChildBackoffActor", // a child call simpleChildBackoffActor is being created
      1 seconds, // this value * 2 for the next value. E.g first attempt is 1s, 2s, 4s, 8s, 16s
      30 seconds, //max cap at 30 seconds
      0.2
    )
  )

  val stopParentSupervisor = system.actorOf(simpleSupervisorProps, "stopParentSupervisor")
}

