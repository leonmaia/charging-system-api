package unit.com.newmotion.core.fees

import com.newmotion.core.fees.Fee
import com.newmotion.core.tariffs.Tariff
import com.newmotion.core.transactions.Transaction
import com.newmotion.util.DateSupport
import org.joda.time.LocalDateTime
import unit.com.newmotion.core.BaseSpec

class FeeSpec extends BaseSpec {
  val ds = new DateSupport

  behavior of "#apply with valid tariff"

  it should "calculate fees correctly" in {
    val transaction = Transaction("john",s"$nextYear-10-28T09:34:17Z",s"$nextYear-10-28T16:45:13Z", 32.03)
    val tariff = Tariff(1.50, 0.50, 0.30, s"$nextYear-10-28T00:00:00Z") :: Nil
    val fee = Fee(transaction, tariff).get

    fee.hourlyFee should be(3.59D)
    fee.kWhFee should be(9.61D)
    fee.total should be(14.70D)
  }

  behavior of "#apply without valid tariff"

  it should "calculate fees correctly" in {
    val transaction = Transaction("john",s"$nextYear-10-28T09:34:17Z",s"$nextYear-10-28T16:45:13Z", 32.03)
    val tariff = Tariff(1.50, 0.50, 0.30, s"${nextYear+1}-10-28T00:00:00Z") :: Nil

    Fee(transaction, tariff) should be(Option.empty)
  }

  behavior of "#bringCorrectTariff"

  it should "select latest tariff with activeStarting before transaction startTime" in {
    val transaction = Transaction("john",s"$nextYear-10-28T08:34:17Z",s"$nextYear-10-28T16:45:13Z", 32.03)

    val tariffs = Tariff(1.50, 0.50, 0.30, s"$nextYear-10-28T09:00:00Z") ::
                  Tariff(1.00, 0.40, 0.20, s"${nextYear - 1}-10-28T05:00:00Z") ::
                  Tariff(1.66, 1.39, 0.55, s"$nextYear-10-28T10:00:00Z") :: Nil

    val correct = Fee.bringCorrectTariff(ds.parse(transaction.startTime), tariffs).get

    correct.startFee should be(1.00D)
    correct.hourlyFee should be(0.40D)
    correct.feePerKWh should be(0.20D)
    correct.activeStarting should be(s"${nextYear - 1}-10-28T05:00:00Z")
  }

}
