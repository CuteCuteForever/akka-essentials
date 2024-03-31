package section5

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props, Terminated}

/**
 *  Using context.watch(ActorRef) to register the self actor to the ActorRef to monitor it. If the ActorRef dies, the self actor will be notified
 *  Naturally use to guarantee that the actorRef does not died because you need a response to it.
 *  After you receive the respond, then you will unwatch it
 */
object scala_24_04_Context_watch extends App {

  implicit val system = ActorSystem("Deathwatch")

  case class StartChild(name: String)
  case class StopChild(name: String)
  case object Stop


  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  class Watcher extends Actor with ActorLogging {

    override def receive: Receive = {
      case StartChild(name) =>
        val child = context.actorOf(Props[Child], name)
        log.info(s"Started and watching child $name")
        context.watch(child) //register self actor to watch a this child actor. If the child die, the self actor will be notified
      case Terminated(ref) => //akka automatically send this Terminated message when the "watched child actor" dies
        log.info(s"the reference that I'm watching $ref has been stopped")
    }
  }

  val watcher = system.actorOf(Props[Watcher], "watcher")
  watcher ! StartChild("watchedChild")
  val watchedChild = system.actorSelection("/user/watcher/watchedChild")
  Thread.sleep(500)

  watchedChild ! PoisonPill


}

