package unit.com.newmotion.core.fees

import com.newmotion.core.fees.Fee
import com.newmotion.core.tariffs.Tariff
import com.newmotion.core.transactions.Transaction
import org.joda.time.LocalDateTime
import unit.com.newmotion.core.BaseSpec

class FeeSpec extends BaseSpec {
  val nextYear = LocalDateTime.now().getYear + 1

  it should "calculate hourly fee correctly" in {
    val transaction = Transaction("john",s"$nextYear-10-28T09:34:17Z",s"$nextYear-10-28T16:45:13Z", 32.03)
    val tariff = Tariff(1.50, 0.50, 0.30, s"$nextYear-10-28T00:00:00Z")
    val fee = Fee(transaction, tariff)

    fee.hourlyFee should be(3.59D)
    fee.kWhFee should be(9.61D)
    fee.total should be(14.70D)
  }
}
