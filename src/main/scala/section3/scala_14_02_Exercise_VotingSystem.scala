package section3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object scala_14_02_Exercise_VotingSystem extends App {

  val system = ActorSystem("actorSystem")

  case class Vote(candidate: String)
  case object VoteStatusRequest
  case class VoteStatusReply(candidiate: Option[String])

  class Citizen extends Actor {
    override def receive: Receive = {
      case Vote(c) => context.become(voted(c))
      case VoteStatusRequest => sender() ! VoteStatusReply(None)
    }

    def voted(c: String): Receive = {
      case VoteStatusRequest => sender() ! VoteStatusReply(Some(c))
    }
  }

  case class AggregateVotes(citizen: Set[ActorRef])
  case object Print

  class VoteAggregator extends Actor {

    override def receive: Receive = awaitingCommand

    def awaitingCommand: Receive = {
      case AggregateVotes(citizenSet) => citizenSet.foreach(actor => actor ! VoteStatusRequest)
      context.become(awaitingStatuses(citizenSet, Map()))
    }

    def awaitingStatuses(citizenSet: Set[ActorRef], map: Map[String, Int]): Receive = {
      case VoteStatusReply(None) => sender() ! VoteStatusRequest

      case VoteStatusReply(Some(candidate)) => {
        val newCitizenSet = citizenSet - sender()
        val currentVotesOfCandidate: Int = map.getOrElse(candidate, 0)
        val newMap: Map[String, Int] = map + (candidate -> (currentVotesOfCandidate+1))

        if (newCitizenSet.isEmpty){
          println(s"[aggregator] poll stats: $map")
        } else {
          context.become(awaitingStatuses(newCitizenSet, newMap))
        }
      }
    }
  }

  val alice = system.actorOf(Props[Citizen])
  val bob = system.actorOf(Props[Citizen])
  val charlie = system.actorOf(Props[Citizen])
  val daniel = system.actorOf(Props[Citizen])

  alice ! Vote("Martin")
  bob ! Vote("Jonas")
  charlie ! Vote("Roland")
  daniel ! Vote("Roland")

  val voteAggregator = system.actorOf(Props[VoteAggregator])
  voteAggregator ! AggregateVotes(Set(alice, bob, charlie, daniel))

}