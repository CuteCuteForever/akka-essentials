package section6

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.{FromConfig, RoundRobinGroup}
import com.typesafe.config.ConfigFactory
import section6.scala_29_03_Routers_configuration.simplePoolMaster


/**
 * Group routees are applicable where your routees are initialized in other place of your code and you just reuse it
 */
object scala_29_04_Routers_group extends App {

  class Slave extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("GroupRouter")

  //lets say in another part of application, it have a list of slaves ready to be used
  val slaveList = (1 to 5).map( i => system.actorOf(Props[Slave], s"slave_$i")).toList

  //need their path
  val slavePaths: List[String] = slaveList.map(slaveRef => slaveRef.path.toString)

  val groupMaster = system.actorOf(RoundRobinGroup(slavePaths).props())
  for (i <- 1 to 10) {
    groupMaster ! s"[$i]Hello from the world"
  }

}

