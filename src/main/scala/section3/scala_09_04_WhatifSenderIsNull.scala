package section3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object scala_09_04_WhatifSenderIsNull extends App {

  val system = ActorSystem("actorCapabilitiesDemo")

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case "Hi" => context.sender() ! "Hello There!"
    }
  }

  // 4 - dead letters
  val alice = system.actorOf(Props[SimpleActor], "Alice")
  alice ! "Hi" //dead letters are like garbage pool
}