package section5

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, AllForOneStrategy, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import section5.scala_26_01_Supervision.{NoDeathOnRestartSupervisor, _}

/**
 * SupervisorStrategy helps to determine the state of the Actor using directive
 *
 * OneForOneStrategy -> apply that decision to the actor that cause the failure
 * AllForOneStrategy -> apply strategy that if one children fail the exception, all of the children will be affected the same.
 *    Example if the first children encounter an exception and need to restart, all the children have to be restarted too.
 */
class scala_26_01_Supervision extends TestKit(ActorSystem("SupervisionSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A supervisor" should {
    "Child state does not changed when the 'child' (Resume) when faced upon RuntimeException" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[ChildActor]
      val child = expectMsgType[ActorRef]

      child ! "I love Akka"
      child ! Report
      expectMsg(3)

      child ! RunTimeExceptionMsg
      child ! Report // the state of the child is still not changed although throw a runTimeException,
      expectMsg(3)
    }

    "restart its child when faced upon NullPointerException" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[ChildActor]
      val child = expectMsgType[ActorRef]

      child ! "I love Akka"
      child ! Report
      expectMsg(3)

      child ! NullPointerExceptionMsg
      child ! Report
      expectMsg(0) // the state is changed when it is being "Restart"
    }

    "stop/terminate its child when faced upon IllegalArgumentException" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[ChildActor]
      val child = expectMsgType[ActorRef]

      watch(child) //watch a child with TestKit scala and expect a 'Terminated' msg if the child is being stop

      child ! IllegalArgumentExceptionMsg
      val terminatedMessage = expectMsgType[Terminated]
      assert(terminatedMessage.actor == child)
    }

    /**
     * if you run this test case, you will see the logs that the child escalated the exception to its parent
     */
    "escalate when faced upon Exception" in {
      val supervisor = system.actorOf(Props[Supervisor], "EscalatedSupervisor")
      supervisor ! Props[ChildActor]
      val child = expectMsgType[ActorRef]

      watch(child) //watch a child with TestKit scala and expect a 'Terminated' msg if the child is being stop

      child ! ExceptionMsg // will escalate its exception to parent and child actor will die
      val terminatedMessage = expectMsgType[Terminated]
      assert(terminatedMessage.actor == child)
    }
  }

  "A noDeathOnRestartSupervisor" should {
    "not kill children in case its restart" in {
      val noDeathOnRestartSupervisor = system.actorOf(Props[NoDeathOnRestartSupervisor], "NoDeathOnRestartSupervisor")
      noDeathOnRestartSupervisor ! Props[ChildActor]
      val child = expectMsgType[ActorRef]

      child ! "I love Akka"
      child ! Report
      expectMsg(3)

      child ! ExceptionMsg
      child ! Report
      expectMsg(0) // when restart, all the state are gone
    }
  }

  "An all-for-one-supervisor" should {
    "apply the all-for-one strategy" in {
      val allForOneSupervisor = system.actorOf(Props[AllForOneSupervisor], "allForOneSupervisor")
      allForOneSupervisor ! Props[ChildActor]
      val child = expectMsgType[ActorRef]

      allForOneSupervisor ! Props[ChildActor]
      val secondChild = expectMsgType[ActorRef]

      secondChild ! "Testing supervision" //normal message that will be process
      secondChild ! Report
      expectMsg(2)

      EventFilter[NullPointerException]() intercept {
        child ! NullPointerExceptionMsg //send a NullPointerException, that cause Restart to happen
      }

      Thread.sleep(500)

      secondChild ! Report //second child will be restarted because AllForOneStrategy being used and thus causing num words to return 0
      expectMsg(0)
    }
  }

}

object scala_26_01_Supervision {

  case object Report
  case object RunTimeExceptionMsg
  case object IllegalArgumentExceptionMsg
  case object NullPointerExceptionMsg
  case object ExceptionMsg

  class Supervisor extends Actor {

    override val supervisorStrategy : SupervisorStrategy = OneForOneStrategy() { //member of the akka trait
      case _ : NullPointerException => Restart //it is a directive
      case _ : IllegalArgumentException => Stop //it is a directive
      case _ : RuntimeException => Resume //it is a directive
      case _ : Exception => Escalate //it is a directive
    }

    override def receive: Receive = {
      case props: Props => //Props is the wrapper instance of ActorRef
        val childRef = context.actorOf(props)
        sender() ! childRef
    }
  }

  class AllForOneSupervisor extends Supervisor {
    override val supervisorStrategy = AllForOneStrategy() {
      case _: NullPointerException => Restart //it is a directive
      case _: IllegalArgumentException => Stop //it is a directive
      case _: RuntimeException => Resume //it is a directive
      case _: Exception => Escalate //it is a directive
    }
  }

  class NoDeathOnRestartSupervisor extends Supervisor {
    override def preRestart(reason: Throwable, message: Option[Any]): Unit = {}
  }

  class ChildActor extends Actor with ActorLogging{
    var words = 0

    override def receive: Receive = {
      case Report => sender() ! words
      case NullPointerExceptionMsg => throw new NullPointerException()
      case RunTimeExceptionMsg => throw new RuntimeException()
      case IllegalArgumentExceptionMsg => throw new IllegalArgumentException()
      case ExceptionMsg => throw new Exception()
      case message: String => {
        log.info(s"message is $message")
        words += message.split(" ").length
      }
    }
  }
}

