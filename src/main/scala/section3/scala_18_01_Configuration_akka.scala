package section3

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging
import com.typesafe.config.ConfigFactory

object scala_18_01_Configuration_akka extends App {

  //val system = ActorSystem("actorSystem")
  class SimpleLoggingActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  /**
   * #1 - Inline String Configuration , //remember it always start with akka{...} namespace
   */
  val configString =
    """
      | akka {
      |   loglevel = "ERROR"
      | }
      |""".stripMargin

  val config = ConfigFactory.parseString(configString)
  val system = ActorSystem("ConfigurationDemo", ConfigFactory.load(config)) //system level to insert the config
  val simpleLoggingActor = system.actorOf(Props[SimpleLoggingActor], "SimpleLoggingActor")

  simpleLoggingActor ! "A message to remember"

  /**
   * // #2 - Using resource application.conf file
   */
  val defaultConfigFileSystem = ActorSystem("DefaultConfigFileDemo") //when config is not mention, it will look at resources/application.conf file
  val defaultConfigActor = defaultConfigFileSystem.actorOf(Props[SimpleLoggingActor])
  defaultConfigActor ! "Remember me"

  /**
   * // #3 - Separate config in the same file
   */
  val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
  val specialConfigSystem = ActorSystem("SpecialConfigDemo", specialConfig) //when config is not mention, it will look at resources/application.conf file
  val specialConfigActor = specialConfigSystem.actorOf(Props[SimpleLoggingActor])
  specialConfigActor ! "Remember me ! i am special"

  /**
   * #4 - Separate config in the another file
   */
  val secretConfiguration = ConfigFactory.load("secret/secretConfiguration.conf")
  println(s"Separate config log level: ${secretConfiguration.getString("akka.loglevel")}")

  /**
   * 5 - different file formatsJSON
   */
  val jsonConfiguration = ConfigFactory.load("json/jsonConfig.json")
  println(s"json config log level: ${jsonConfiguration.getString("aJsonProperty")}")
  println(s"json config log level: ${jsonConfiguration.getString("akka.loglevel")}")

  /**
   * 6 - different file formats Properties file
   */
  val propsConfiguration = ConfigFactory.load("property/propsConfiguration.properties")
  println(s"properties config log level: ${propsConfiguration.getString("my.simpleProperty")}")
  println(s"properties config log level: ${propsConfiguration.getString("akka.loglevel")}")
}