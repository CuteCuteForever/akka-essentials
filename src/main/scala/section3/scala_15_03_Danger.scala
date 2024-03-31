package section3

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import section3.scala_15_03_Danger.CreditCard.AttachToAccount

/* Learning Points
  - Every interaction with actor must happen through msg handler and not through method calling
 */
object scala_15_03_Danger extends App {

  val system = ActorSystem("actorSystem")

  object NaiveBankAccount {
    case class Deposit(amount: Int)
    case class Withdraw(amount: Int)
    case object CreateCreditCardAndAttached
  }
  class NaiveBankAccount extends Actor {
    import NaiveBankAccount._
    import CreditCard._

    var amount = 0

    override def receive: Receive = {
      case CreateCreditCardAndAttached =>
        val creditCardRef = context.actorOf(Props[CreditCard],"card")
        creditCardRef ! AttachToAccount(this) // Danger - Exposing the object methods calls to other actors. Every interaction with actor must happen through msg handler and not through method calling
      case Deposit(funds) => deposit(funds)
      case Withdraw(funds) => withdraw(funds)
    }

    def deposit(funds: Int) = {
      println(s"${self.path} depositing $funds on top of $amount")
      amount += funds
    }

    def withdraw(funds: Int) = {
      println(s"${self.path} withdrawing $funds on top of $amount")
      amount -= funds
    }
  }

  object CreditCard {
    case class AttachToAccount(bankAccount: NaiveBankAccount) // Danger - should not have object as parameters
    case object CheckStatus
  }
  class CreditCard extends Actor {
    import CreditCard._

    override def receive: Receive = {
      case AttachToAccount(account) => context.become(attachTo(account))
    }

    def attachTo(account: NaiveBankAccount) : Receive = {
      case CheckStatus =>
        println(s"${self.path} your message has been processed")
        account.withdraw(1) // Danger - There should not be any any method calls on any actor. Everything should be done via msg handler
    }
  }

  import NaiveBankAccount._
  import CreditCard._

  val bankAccountActor = system.actorOf(Props[NaiveBankAccount], "account")
  bankAccountActor ! CreateCreditCardAndAttached
  bankAccountActor ! Deposit(100)

  Thread.sleep(500)

  val creditCardSelection = system.actorSelection("/user/account/card")
  creditCardSelection ! CheckStatus
}