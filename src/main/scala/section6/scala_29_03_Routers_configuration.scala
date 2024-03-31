package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.{FromConfig, RoundRobinPool}
import com.typesafe.config.ConfigFactory


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
object scala_29_03_Routers_configuration extends App {

  class Slave extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("RoutersDemo", ConfigFactory.load().getConfig("routersDemo"))
  val simplePoolMaster = system.actorOf(FromConfig.props(Props[Slave]), "poolMaster2")
  for (i <- 1 to 10) {
    simplePoolMaster ! s"[$i]Hello from the world"
  }
}

