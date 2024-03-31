package section3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import section3.scala_09_06_Exercise_BankAccount.BankAccount.Deposit
import section3.scala_09_06_Exercise_BankAccount.Person.LifeTheLife

import scala.util.{Failure, Success}

object scala_09_06_Exercise_BankAccount extends App {

  val system = ActorSystem("actorSystem")

  object BankAccount {
    case class Deposit(value: Int)
    case class Withdraw(value: Int)
    case object Statement
    case class TransactionSuccess(message: String)
    case class TransactionFailure(message: String)
  }

  class BankAccountActor extends Actor {
    import BankAccount._

    var funds: Int = 0;
    override def receive: Receive = {
      case Deposit(amount: Int) => {
        if (funds < 0) sender() ! TransactionFailure("invalid deposit amount")
        else {
          funds += amount
          sender() ! TransactionSuccess(s"Successfully deposit $amount")
        }
      }
      case Withdraw(amount) => {
        if (amount < 0) sender() ! TransactionFailure("invalid withdraw amount")
        else if (amount > funds) sender() ! TransactionFailure("insufficient funds")
        else {
          funds -= amount
          sender() ! TransactionSuccess(s"successfully withdrawn $amount")
        }
      }
      case Statement => sender() ! s"Your balance is $funds"
    }
  }

  object Person {
    case class LifeTheLife(bankAccount: ActorRef)
  }

  class Person extends Actor {
    import Person._
    import BankAccount._

    override def receive: Receive = {
      case LifeTheLife(bankAccount: ActorRef) => {
        bankAccount ! Deposit(10000)
        bankAccount ! Withdraw(90000)
        bankAccount ! Withdraw(500)
        bankAccount ! Statement
      }
      case message => println(message.toString)
    }
  }

  val bankAccountActor = system.actorOf(Props[BankAccountActor], "bankAccountActor")
  val personActor = system.actorOf(Props[Person], "personActor")

  personActor ! LifeTheLife(bankAccountActor)

}