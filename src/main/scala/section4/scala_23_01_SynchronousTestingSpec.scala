package section4

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{CallingThreadDispatcher, EventFilter, ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import scala.concurrent.duration._

/**
 *  Synchronous tests: All messages are handled in the calling thread instead of asynchronously that does it on different thread. Sometimes
 *  doing asynchronously might be slower than all operations done on the same calling thread.
 *  Asynchronous operations might also be instable in certain ways during testing.
 *
 *  Option 1: TestActorRef
 *  Option 2: Calling threadDispatcher
 *
 */
class scala_22_01_SynchronousTestingSpec extends  WordSpecLike with BeforeAndAfterAll {

  implicit val system = ActorSystem("SynchronousTestingSpec")

  override def afterAll(): Unit = {
    system.terminate();
  }

  import SynchronousTestingSpec._

  "A counter" should {
    "synchronously increase its counter" in {
      val counterActor = TestActorRef[CounterActor](Props[CounterActor])
      counterActor ! Inc //counterActor  has ALREADY received the message
      assert(counterActor.underlyingActor.count == 1)
    }

    "synchronously increase its counter at the call of the receive function" in {
      val counterActor = TestActorRef[CounterActor](Props[CounterActor]) //having it to be on same thread
      counterActor.receive(Inc)
      assert(counterActor.underlyingActor.count == 1)
    }

    "work on the calling thread dispatcher" in {
      val counterActor = system.actorOf(Props[CounterActor].withDispatcher(CallingThreadDispatcher.Id)) //means having the counterActor to be on the same calling thread
      //val counterActor = system.actorOf(Props[CounterActor]) //means having it on another thread which will take time for probe to receive the "Read" message
      val probe = TestProbe()
      probe.send(counterActor, Read)
      probe.expectMsg(Duration.Zero,0)
    }
  }
}

object SynchronousTestingSpec {

  case object Inc
  case object Read

  class CounterActor extends Actor {
    var count = 0

    override def receive: Receive = {
      case Inc => count += 1
      case Read => sender() ! count
    }
  }



}




