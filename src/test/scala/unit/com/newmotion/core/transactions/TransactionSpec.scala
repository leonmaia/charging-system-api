package unit.com.newmotion.core.transactions

import com.fasterxml.jackson.core.JsonParseException
import com.newmotion.core.transactions.Transaction
import unit.com.newmotion.core.BaseSpec

class TransactionSpec extends BaseSpec {

  behavior of "valid json"

  it should "parse correctly" in {
    val body =
      """
        |{
        |"customerId": "john",
        |"startTime": "2014-10-28T09:34:17Z",
        |"endTime": "2014-10-28T16:45:13Z",
        |"volume": 32.03
        |}""".stripMargin
    val transaction = Transaction(buildRequest(body.toString))

    transaction.id should be("john")
    transaction.startTime should be("2014-10-28T09:34:17Z")
    transaction.endTime should be("2014-10-28T16:45:13Z")
    transaction.volume should be(32.03)
  }

  behavior of "invalid json"

  it should "fail on endTime requirement" in {
    intercept[NullPointerException] {
      val body =
        """
          |{
          |"customerId": "john",
          |"startTime": "2014-10-28T09:34:17Z",
          |"volume": 32.03
          |}""".stripMargin
      Transaction(buildRequest(body.toString))
    }
  }

  it should "fail on customerId requirement" in {
    intercept[NullPointerException] {
      val body =
        """
          |{
          |"startTime": "2014-10-28T09:34:17Z",
          |"endTime": "2014-10-28T16:45:13Z",
          |"volume": 32.03
          |}""".stripMargin
      Transaction(buildRequest(body.toString))
    }
  }

  it should "fail on startTime requirement" in {
    intercept[NullPointerException] {
      val body =
        """
          |{
          |"customerId": "john",
          |"endTime": "2014-10-28T16:45:13Z",
          |"volume": 32.03
          |}""".stripMargin
      Transaction(buildRequest(body.toString))
    }
  }

  it should "fail on parsing" in {
    intercept[JsonParseException] {
      val body =
        """something very invalid""".stripMargin
      Transaction(buildRequest(body.toString))
    }
  }

  behavior of "calculation of duration in decimal"

  it should "calculate duration in decimal correctly" in {
    val t0 = new Transaction(id = "pete", startTime = "2014-10-27T13:32:14Z",
      endTime = "2014-10-27T14:32:14Z", volume = 13.21)
    val t1 = new Transaction(id = "pete", startTime = "2014-10-27T13:32:14Z",
      endTime = "2014-10-27T15:02:14Z", volume = 13.21)
    val t2 = new Transaction(id = "pete", startTime = "2014-10-27T13:32:14Z",
      endTime = "2014-10-27T15:04:14Z", volume = 13.21)

    t0.durationInDecimal should be(1.00D)
    t1.durationInDecimal should be(1.50D)
    t2.durationInDecimal should be(1.53D)
  }
}
