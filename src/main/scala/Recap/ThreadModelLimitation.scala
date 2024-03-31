package Recap

object ThreadModelLimitation extends App{

  class BankAccount(@volatile var amount:Int) {
    override def toString: String = s" $amount"

    def withdraw(money: Int) = this.synchronized{
      this.amount -= money
    }
    def deposit(money: Int) = this.synchronized{
      this.amount += money
    }
    def getAmount = amount
  }

  val account = new BankAccount(2000)
  println(account.getAmount)

  for ( _ <- 1 to 1000) {
    new Thread(()=>account.withdraw(1)).start()
  }

  println(account.getAmount)

  for (_ <- 1 to 1000) {
    new Thread(() => account.deposit(1)).start()
  }

    println(account.getAmount)


}
