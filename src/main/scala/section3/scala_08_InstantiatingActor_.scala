package section3

import akka.actor.{Actor, ActorSystem, Props}

object scala_08_InstantiatingActor_ extends  App {

  //part 1 - Create ActorSystem
  val actorSystem = ActorSystem("firstActorSystem")
  println(actorSystem.name)

  //part2 - create actor that count word

  class WordCountActor extends Actor {
    //internal data
    var totalWords = 0

    //behaviour
    override def receive: PartialFunction[Any, Unit] = {
      case message: String => {
        println(s"[word counter] I have received: ${message}")
        totalWords += message.split(" ").length
      }
      case msg => println(s"[word counter] I cannot understand ${msg.toString}")
    }
  }

  //part 3 - create an actor instance
  val wordCounterActor = actorSystem.actorOf(Props[WordCountActor] , "wordCounterActor")

  val wordCounterActor2 = actorSystem.actorOf(Props[WordCountActor] , "wordCounterActor2")

  //part 4 - communicate with our actor asynchronously
  wordCounterActor ! "I am learning Akka and its cool"
  wordCounterActor2 ! "A difference message"

  //part 5 - Creating Actor with constructor parameters
  class Person(name: String) extends Actor {
    override def receive: PartialFunction[Any, Unit] = {
      case "hi" => println(s"Hi, my name is $name")
      case _ =>
    }
  }

  //best practice to create new Actor instance with constructor arguments
  object Person{
    def props(name: String) = Props(new Person(name))
  }

  val personActor = actorSystem.actorOf(Person.props("bob"), "personActor")
  personActor ! "hi"
}
