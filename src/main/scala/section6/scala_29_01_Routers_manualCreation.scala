package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Terminated, Timers}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Routee, Routees, Router}


/**
 * RoundRobin logic
 * a) round robin
 * b) random
 * c) smallest mailbox -. send the message to the actor with fewest message in quque
 * d) broadcast - send all routee
 * e) scatter-gather-first - broadcast and wait for first reply. Then the remaining replies are discarded
 * f) tail-chopping - forward message sequentially to each routee and wait for first reply
 * g) consistent-hashing - all message with same hash goes to same actor
 */
object scala_29_01_Routers_manualCreation extends App {

  /**
   * Manual Router
   */
  class Master extends Actor with ActorLogging {

    // Step 1 - Define your routee
    private val slavesSeq = for ( i <- 1 to 5) yield {
      val slave = context.actorOf(Props[Slave], s"Slave_$i")
      context.watch(slave)
      ActorRefRoutee(slave)
    }

    // Step 2 - Define your router and routing logic
    private val router = Router(RoundRobinRoutingLogic(), slavesSeq)

    override def receive: Receive = {
      //Step 4 - handling your lifecycle of Routee if it died
      case Terminated(ref) =>
        router.removeRoutee(ref) //addRoutee and removeRoutee return a new router
        val newSlave = context.actorOf(Props[Slave])
        router.addRoutee(newSlave)
      //Step 3 - handling your message
      case message =>
        router.route(message, sender())
    }
  }

  class Slave extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("RoutersDemo")
  val master = system.actorOf(Props[Master])

  for ( i <- 1 to 10) {
    master ! s"[$i]Hello from the world"
  }

}

