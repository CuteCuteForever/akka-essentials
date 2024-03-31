package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.{Broadcast, FromConfig}
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
object scala_29_04_Routers_group_configuration extends App {

  class Slave extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("GroupRouter", ConfigFactory.load().getConfig("routersDemo"))

  //lets say in another part of application, it have a list of slaves ready to be used
  val slaveList = (1 to 5).map( i => system.actorOf(Props[Slave], s"slave_$i")).toList

  //You dont need the path anymore as they are getting from config
  //val slavePaths: List[String] = slaveList.map(slaveRef => slaveRef.path.toString)

  val groupMaster = system.actorOf(FromConfig.props(), "groupMaster2")
  for (i <- 1 to 10) {
    groupMaster ! s"[$i]Hello from the world"
  }

  /**
   * Special messages
   */
  groupMaster ! Broadcast("Hello, everyone")

}

