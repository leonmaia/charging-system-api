package unit.com.chargingsystem.core.transactions

import com.chargingsystem.core.transactions.{Filter, TransactionFilter, Transaction}
import unit.com.chargingsystem.core.BaseSpec

class TransactionFilterSpec extends BaseSpec {

  val t0 = new Transaction(id = "pete", startTime = "2015-10-27T13:32:14Z",
    endTime = "2015-10-27T14:32:14Z", volume = 13.21)
  val t1 = new Transaction(id = "pete", startTime = "2014-10-27T13:32:14Z",
    endTime = "2014-10-27T15:02:14Z", volume = 10.02)
  val t2 = new Transaction(id = "pete", startTime = "2014-10-27T13:32:14Z",
    endTime = "2014-10-27T15:04:14Z", volume = 25.11)
  val t3 = new Transaction(id = "pete", startTime = "2015-08-27T13:32:14Z",
    endTime = "2015-08-27T15:04:14Z", volume = 32.01)

  val f0 = Filter(field = "customer_name", value = "pete")
  val f1 = Filter(field = "year", value = "2015")
  val f2 = Filter(field = "month", value = "10")

  behavior of "#filter"

  it should "return filtered transactions" in {
    val filters      = f0 :: f1 :: f2 :: Nil
    val transactions = t0 :: t1 :: t2 :: t3 :: Nil

    val filtered = TransactionFilter.filter(transactions, filters).get
    filtered.size should be(1)
    filtered.head.startTime should be("2015-10-27T13:32:14Z")
    filtered.head.volume should be(13.21D)
  }

  it should "return empty list when no match" in {
    val filters      = f0.copy(value = "leon") :: f1 :: f2 :: Nil
    val transactions = t0 :: t1 :: t2 :: t3 :: Nil

    TransactionFilter.filter(transactions, filters) should be(Option.empty)
  }
}
