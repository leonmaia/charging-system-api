package unit.com.chargingsystem.core.tariffs

import com.chargingsystem.core.tariffs.Tariff
import org.joda.time.LocalDateTime
import unit.com.chargingsystem.core.BaseSpec

class TariffSpec extends BaseSpec {

  behavior of "valid json"

  it should "parse correctly" in {
    val body =
      s"""{
         |"startFee": 0.20,
         |"hourlyFee": 1.00,
         |"feePerKWh": 0.25,
         |"activeStarting": "$nextYear-10-28T06:00:00Z"
         |}""".stripMargin
    val tariff = Tariff(buildRequest(body.toString))

    tariff.startFee should be(0.20D)
    tariff.hourlyFee should be(1.00D)
    tariff.feePerKWh should be(0.25D)
    tariff.activeStarting should be(s"$nextYear-10-28T06:00:00Z")
  }

  behavior of "invalid json"

  it should "fail on requirements" in {
    intercept[NullPointerException] {
      val body =
        s"""{
           |"startFee": 0.20,
           |"hourlyFee": 1.00,
           |"activeStarting": "$nextYear-10-28T06:00:00Z"
           |}""".stripMargin
      Tariff(buildRequest(body.toString))
    }
  }

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

  behavior of "#fromCSV"

  it should "return a Tariff" in {
    val body =
      s"""{
         |"startFee": 0.20,
         |"hourlyFee": 1.00,
         |"feePerKWh": 0.25,
         |"activeStarting": "$nextYear-10-28T06:00:00Z"
                                        |}""".stripMargin
    val t = Tariff(buildRequest(body.toString))
    val key = s"${t.activeStarting},${t.startFee},${t.hourlyFee},${t.feePerKWh}"
    val tFromKey = Tariff.fromCSV(key)

    tFromKey.startFee should be(0.20D)
    tFromKey.hourlyFee should be(1.00D)
    tFromKey.feePerKWh should be(0.25D)
    tFromKey.activeStarting should be(s"$nextYear-10-28T06:00:00Z")
  }

  behavior of "#createCSVKey"
  it should "return a Tariff" in {
    val t = Tariff(1.00D, 0.25D, 0.50D, s"$nextYear-10-28T06:00:00Z").createCSVKey
    t should be(s"$nextYear-10-28T06:00:00Z,1.0,0.25,0.5")
  }
}
