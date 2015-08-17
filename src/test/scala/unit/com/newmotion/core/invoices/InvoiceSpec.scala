package unit.com.newmotion.core.invoices

import com.newmotion.core.invoices.Invoice
import com.newmotion.core.transactions.Transaction
import unit.com.newmotion.core.BaseSpec

class InvoiceSpec extends BaseSpec {

  behavior of "#sum"
  it should "sum correctly" in {
    val result = Invoice.sum(BigDecimal(1.00D) :: BigDecimal(13.33D) :: BigDecimal(0.23D) :: Nil)

    result should be(14.56D)
  }

  behavior of "#apply with empty list"
  it should "return Option.empty" in {
    Invoice(Option.empty) should be(Option.empty)
  }

  behavior of "#apply with valid list"
  it should "return invoice" in {
    val t0 = Transaction(id = "pete", startTime = "2014-10-27T13:32:14Z",
      endTime = "2014-10-27T14:32:14Z", volume = 13.21, total = Option(1.00D))

    Invoice(Option(t0 :: Nil)).get.name should be("pete")
    Invoice(Option(t0 :: Nil)).get.total should be(1.00D)
    Invoice(Option(t0 :: Nil)).get.month should be("October")
    Invoice(Option(t0 :: Nil)).get.year should be(2014)
    Invoice(Option(t0 :: Nil)).get.charges should be("from 2014-10-27 13:32 to 2014-10-27 14:32: 13.21 kWh @ â‚¬ 1.0")
  }

  behavior of "#apply with invalid transaction"
}
