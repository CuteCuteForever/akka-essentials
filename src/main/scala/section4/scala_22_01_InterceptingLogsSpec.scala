package section4

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}


/**
 * A few hard to test scenario
 * 1) CheckoutActor automatically create 2 new Actor (FulfillmentManager & PaymentManager). It is not done via injection
 * 2) CheckoutActor does not return any value to the guardian level actor (E.g. testActor)
 *
 * Solution: Use EventFilter to scan for LOG messages using "akka.testKit.TestEventListener" in properties file
 */
class scala_22_01_InterceptingLogsSpec extends TestKit(ActorSystem("InterceptingLogsSpec" , ConfigFactory.load().getConfig("interceptingLogMessages")))
  with ImplicitSender //passing testActor as the implicit sender for every single message
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import InterceptingLogsSpec._

  val item = "Rock the JVM Akka course"
  val creditCard = "1234-1234-1234-1234"
  val invalidCreditCard = "0000-0000-0000-0000"

  /**
   *  create an eventFilter object that scan for log messages at level INFO.
   *  However you need to configure the logger in properties file "akka.testkit.TestEventListener"
   *  so that the logger can output it to an appropriate space so that the event filter is able to capture it.
   *
   *  Note that intercept method wait for a specific amount of time only to capture the log message output
   *  via "akka.test.filter-leeway" in property file
   *
   */

  "A checkout flow" should {
    "correctly log the dispatch of an order" in {
      EventFilter.info(pattern = s"Order [0-9]+ for item $item has been dispatched.", occurrences = 1) intercept {
        val checkoutRef = system.actorOf(Props[CheckoutActor])
        checkoutRef ! Checkout(item, creditCard)
      }
    }

    "freak out if the payment is denied" in {
      EventFilter[RuntimeException](occurrences = 1) intercept {
        val checkoutRef = system.actorOf(Props[CheckoutActor])
        checkoutRef ! Checkout(item, invalidCreditCard)
      }
    }
  }
}

/**
 *          testActor                   Checkout Actor                    fulFillmentManager                  PaymentManager
 *          checkout ------------------------>
 *                                       AuthorizeCard ------------------------------------------------------------->
 *                                           <---------------------------------------------------------------Payment Accepted
 *                                       DispatchOrder -------------------------->
 *                                           <-------------------------------OrderConfirmed
 */
object InterceptingLogsSpec {

  case class Checkout(item: String, creditCard: String)
  case class AuthorizeCard(creditCard: String)
  case class DispatchOrder(item: String)
  case object PaymentAccepted
  case object PaymentDenied
  case object OrderConfirmed

  class CheckoutActor extends Actor {
    private val paymentManager = context.actorOf((Props[PaymentManager]))
    private val fulfillmentManager = context.actorOf((Props[FulfillmentManager]))
    override def receive: Receive = awaitingCheckout

    def awaitingCheckout: Receive = {
      case Checkout(item, card) =>
        paymentManager ! AuthorizeCard(card)
        context.become(pendingPayment(item))
    }

    def pendingPayment(item: String): Receive = {
      case PaymentAccepted => fulfillmentManager ! DispatchOrder(item)
      context.become(pendingFulfillment(item))
      case PaymentDenied =>
        throw new RuntimeException("I can't handle this anymore")
    }

    def pendingFulfillment(item: String): Receive = {
      case OrderConfirmed => context.become(awaitingCheckout)
    }
  }

  class PaymentManager extends Actor {
    override def receive: Receive = {
      case AuthorizeCard(card) =>
        if (card.startsWith("0")) sender() ! PaymentDenied
        else
          Thread.sleep(4000)
          sender() ! PaymentAccepted
    }
  }

  class FulfillmentManager extends Actor with ActorLogging {
    var orderId = 43
    override def receive: Receive = {
      case DispatchOrder(item: String) =>
        orderId += 1
        log.info(s"Order $orderId for item $item has been dispatched.")
        sender() ! OrderConfirmed
    }
  }


}




