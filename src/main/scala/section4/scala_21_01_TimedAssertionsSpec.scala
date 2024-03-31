package section4

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActors, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.util.Random
import scala.concurrent.duration._

/**
 * TestProbes means mocking the Actor
 */
class scala_21_01_TimedAssertionsSpec extends TestKit(ActorSystem("TimedAssertionsSpec", ConfigFactory.load().getConfig("specialTimedAssertionsConfig")))
  with ImplicitSender //passing testActor as the implicit sender for every single message
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TimedAssertionsSpec._

  "A worker actor" should {
    val workerActor = system.actorOf(Props[WorkerActor])

    "reply with at least 500 milliseconds and at most 1 second" in {
      within(500 millis, 1 second) { //at least 500 millis and at most 1 seconds
        workerActor ! "work"
        expectMsg(WorkResult(42))
      }
    }

    /**
     * max - maximum duration time for processing
     * idle - idle time between those messages
     * messages - total number of messages
     */
    "reply with at most 2 secs" in {
      within(1 second) { //at least 500 millis and at most 1 seconds
        workerActor ! "workSequence"
        val resultSeq: Seq[Int] = receiveWhile[Int](max = 2 seconds, idle= 500 millis, messages=10) {
          case WorkResult(result) => result
        }
        assert(resultSeq.sum > 5)
      }
    }

    /**
     * Even though we indicate a 1 second proccessing time in "within()" method , but this will fail
     * as the timeout from probe is taken from "akka.test.single-expect-default" and not from the "within()"
     */
    "reply to a test probe does not conform to within method" in {
      within(1 second){
        val probe = TestProbe()
        probe.send(workerActor, "work")
        probe.expectMsg(WorkResult(42)) //timeout with 0.3 seconds
      }
    }
  }
}

object TimedAssertionsSpec {

  case class WorkResult(result: Int)

  class WorkerActor extends Actor {
    override def receive: Receive = {
      case "work" =>
        Thread.sleep(500) // simulate long computation
        sender() ! WorkResult(42)
      case "workSequence" =>
        val r = new Random()
        for (_ <- 1 to 10) {
          Thread.sleep(r.nextInt(50))
          sender() ! WorkResult(1)
        }
    }
  }
}




