package section4

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActors, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

/**
 * TestProbes means mocking the Actor
 */
class scala_19_01_TestProbes extends TestKit(ActorSystem("TestProbeSpec"))
  with ImplicitSender //passing testActor as the implicit sender for every single message
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TestProbeSpec._

  "A master actor" should {
    "register a slave" in {
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave") //a test probe is a mock actor in testing

      master ! Register(slave.ref)
      expectMsg(RegistrationAck)
    }

    "send the work to the slave actor" in {
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave") //Mocking the slave
      master ! Register(slave.ref)
      expectMsg(RegistrationAck)

      val workLoadString = "I love Akka"
      master ! Work(workLoadString)

      //the interaction between the master and slave actor
      slave.expectMsg(SlaveWork(workLoadString, testActor))
      slave.reply(WorkCompleted(3, testActor)) //mocking the reply

      expectMsg(Report(3)) //testActor receives the Report(3)
    }

    "aggregate data correctly" in {
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave") //Mocking the slave
      master ! Register(slave.ref)
      expectMsg(RegistrationAck)

      val workLoadString = "I love Akka"
      master ! Work(workLoadString)
      master ! Work(workLoadString)

      /**
       * testActor (Work) -> Master (SlaveWork) -> SlaveActor
       * testActor        <-  (Report) Master   <- (WorkCompleted) SlaveActor
       */

      val abc = slave.receiveWhile() {
        case SlaveWork(`workLoadString`, `testActor`) => slave.reply(WorkCompleted(3, testActor))
      }
      expectMsg(Report(3))
      expectMsg(Report(6))
    }
  }
}

object TestProbeSpec {

  case class Work(text: String)
  case class SlaveWork(text: String, originalRequester: ActorRef)
  case class WorkCompleted(count: Int, originalRequester: ActorRef)
  case class Register(slaveRef: ActorRef)
  case object RegistrationAck
  case class Report(totalCount: Int)

  /**
   *  testActor (Register) -> Master
   *  testActor (Work) -> Master (SlaveWork) -> SlaveActor
   *  testActor        <-  (Report) Master   <- (WorkCompleted) SlaveActor
   */
  class Master extends Actor {
    override def receive: Receive = {
      case Register(slaveRef: ActorRef) => {
        sender() ! RegistrationAck
        context.become(online(slaveRef, 0))
      }
      case _ =>
    }

    def online(slaveRef: ActorRef, totalWordCount: Int) : Receive = {
      case Work(text) => slaveRef ! SlaveWork(text, sender())
      case WorkCompleted(count, originalRequester) =>
        val newTotalWordCount = totalWordCount + count
        originalRequester ! Report(newTotalWordCount) //send back to testActor
        context.become(online(slaveRef, newTotalWordCount))
    }

    // class Slave extends Actor
  }
}




