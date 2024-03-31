package section4

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
import scala.util.Random

/**
 * testActor is the guardian
 */
class scala_19_01_BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender //passing testActor as the implicit sender for every single message. testActor is the guardian
  with WordSpecLike
  with BeforeAndAfterAll {

  //setup
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system) //system is a member of testkit
  }

  import BasicSpec._
  "A simple actor" should {
    "send back the same message" in {
      val echoActor = system.actorOf(Props[SimpleActor])
      val message = "hello, test"
      echoActor ! message
      expectMsg(message) //akka.test.single-expect-default
    }
  }

  "A blackhole actor" should {
    "send back some message" in {
      val blackHoleActor = system.actorOf(Props[BlackHoleActor])
      val message = "hello, test"
      blackHoleActor ! message
      expectNoMessage(1 second)
    }
  }

  "A lab test actor" should {
    val labTestActor = system.actorOf(Props[LabTestActor])
    "turn a string into uppcase" in {
      labTestActor ! "I love Akka"
      val reply = expectMsgType[String] //will return the actual msg back
      assert(reply == "I LOVE AKKA")
    }

    "reply to a greeting" in {
      labTestActor ! "greeting"
      expectMsgAnyOf("hi", "hello")
    }

    "reply with favourite tech" in {
      labTestActor ! "favouriteTech"
      expectMsgAllOf("Scala", "Akka")
    }

    "reply with cool tech in a different way" in {
      labTestActor ! "favouriteTech"
      val message = receiveN(2) //return Seq[Any], ensure that you will receive 2 message
    }

    "reply with cool tech in a fancy way" in {
      labTestActor ! "favouriteTech"
      expectMsgPF() {
        case "Scala" => //PF is defined only for the message we care about
        case "Akka" =>
      }
    }
  }
}

object BasicSpec { //store info/methods/values that going to use in test
  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message => sender() ! message
    }
  }

  class BlackHoleActor extends Actor {
    override def receive: Receive = Actor.emptyBehavior
  }

  class LabTestActor extends Actor {
    val random = new Random()

    override def receive: Receive = {
      case "greeting" =>
        if (random.nextBoolean()) sender() ! "hi" else sender() ! "hello"
      case "favouriteTech" => {
        sender() ! "Scala"
        sender() ! "Akka"
      }
      case message: String => sender() ! message.toUpperCase()
    }
  }
}




