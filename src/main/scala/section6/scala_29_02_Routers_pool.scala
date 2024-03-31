package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinPool, RoundRobinRoutingLogic, Router}


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
object scala_29_02_Routers_pool extends App {

  class Slave extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("RoutersDemo")
  val simplePoolMaster = system.actorOf(RoundRobinPool(5).props(Props[Slave]), "simplePoolMaster")
  for (i <- 1 to 10) {
    simplePoolMaster ! s"[$i]Hello from the world"
  }
}

