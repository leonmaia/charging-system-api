package unit.com.newmotion.core.tariffs

import com.newmotion.core.tariffs.Tariff
import org.joda.time.LocalDateTime
import unit.com.newmotion.core.BaseSpec

class TariffSpec extends BaseSpec {
  val nextYear = LocalDateTime.now().getYear + 1

  behavior of "valid json"

  it should "parse correctly" in {
    val body =
      s"""{
         |"startFee": 0.20,
         |"hourlyFee": 1.00,
         |"feePerKWh": 0.25,
         |"activeStarting": "$nextYear-10-28T06:00:00Z"
         |}""".stripMargin
    val fee = Tariff(buildRequest(body.toString))

    fee.startFee should be(0.20D)
    fee.hourlyFee should be(1.00D)
    fee.feePerKWh should be(0.25D)
    fee.activeStarting should be(s"$nextYear-10-28T06:00:00Z")
  }

  behavior of "invalid json"

  it should "fail on activeStarting requirement" in {
    intercept[NullPointerException] {
      val body =
        s"""{
           |"startFee": 0.20,
           |"hourlyFee": 1.00,
           |"feePerKWh": 0.25
           |}""".stripMargin
      Tariff(buildRequest(body.toString))
    }
  }

  it should "fail on activeStarting requirement invalid date" in {
    intercept[IllegalArgumentException] {
      val body =
        s"""{
           |"startFee": 0.20,
           |"hourlyFee": 1.00,
           |"feePerKWh": 0.25,
           |"activeStarting": "${nextYear - 2}-10-28T06:00:00Z"
           |}""".stripMargin
      Tariff(buildRequest(body.toString))
    }
  }
}
