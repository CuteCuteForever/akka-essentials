package section3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object scala_16_01_Exercise_ChildActors extends App {

  val system = ActorSystem("actorSystem")

  case class Initialize(nChildren: Int)
  case class WordCountTask(id: Int, text: String)
  case class WordCountReply(id: Int, count: Int)

  class WordCounterMaster extends Actor {
    override def receive: Receive = {
      case Initialize(num) => {
        val workerRefs = for (i <- 1 to num) yield context.actorOf(Props[WordCounterWorker], s"worker_$i")
        context.become(withChildren(workerRefs, 0, 0, Map()))
      }
    }

    def withChildren(childrenRefs: Seq[ActorRef], currentChildIndex: Int, currentTaskId: Int, taskIdMap: Map[Int, ActorRef]) : Receive = {
      case text: String => {
        println(s"[master] I have received: $text - I will send it to child $currentChildIndex")
        val task = WordCountTask(currentTaskId, text)
        val childRef = childrenRefs(currentChildIndex)
        childRef ! task
        val nextChildIndex = (currentChildIndex+1) % childrenRefs.length
        val newTaskId = currentTaskId + 1
        val newTaskIdMap = taskIdMap + (currentTaskId -> sender())
        context.become(withChildren(childrenRefs, nextChildIndex, newTaskId, newTaskIdMap))
      }
      case WordCountReply(id, length) =>
        println(s"[master] I have received a reply for task id $id with length $length")
        val originalSender = taskIdMap(id)
        originalSender ! length
        context.become(withChildren(childrenRefs, currentChildIndex, currentTaskId, taskIdMap - id))
    }
  }

  class WordCounterWorker extends Actor {
    override def receive: Receive = {
      case WordCountTask(id, message) => {
        println(s"[${self.path}] - I have received task $id with text $message")
        sender() ! WordCountReply(id, message.split(" ").length)
      }
    }
  }

  class TestActor extends Actor {
    override def receive: Receive = {
      case "go" =>
        val wordCounterMaster = context.actorOf(Props[WordCounterMaster], "wordCounterMaster")
        wordCounterMaster ! Initialize(3)
        val texts = List("I love Akka" , "Scala is super dope", "yes", "me too")
        texts.foreach(text => wordCounterMaster ! text)

      case count: Int => println(s"[Test Actor] I have received a reply of length $count")
    }
  }

  val testActor = system.actorOf(Props[TestActor], "testActor")
  testActor ! "go"
}