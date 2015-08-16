package unit.com.newmotion.core.transactions

import com.newmotion.core.transactions.StoreTransactionHandler
import com.newmotion.models.Fee
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.twitter.util.Await
import org.joda.time.LocalDateTime
import unit.com.newmotion.core.BaseSpec

class ComputeFeeHandlerSpec extends BaseSpec {
  var handler: StoreTransactionHandler = _
  val nextYear = LocalDateTime.now().getYear + 1

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new StoreTransactionHandler with TestRedisStore
  }

  behavior of "#apply"

  it should "parse correctly" in {
    val body =
      s"""{
        |"startFee": 0.20,
        |"hourlyFee": 1.00,
        |"feePerKWh": 0.25,
        |"activeStarting": "$nextYear-10-28T06:00:00Z"
        |}""".stripMargin
    val fee = fromJson[Fee](body)

    fee.startFee should be(0.20)
    fee.hourlyFee should be(1.00)
    fee.feePerKWh should be(0.25)
    fee.activeStarting should be(s"$nextYear-10-28T06:00:00Z")
  }

  it should "return status code 400 if error" in {
    val body =
      s"""{
        |"startFee": 0.20,
        |"hourlyFee": 1.00,
        |"feePerKWh": 0.25,
        |"activeInvalid": "$nextYear-10-28T06:00:00Z"
        |}""".stripMargin

    val response = Await.result(handler.apply(buildRequest(toJson(body))))

    response.statusCode should be(400)
  }

  it should "return status code 400 if activeStarting before now" in {
    val body =
      s"""{
        |"startFee": 0.20,
        |"hourlyFee": 1.00,
        |"feePerKWh": 0.25,
        |"activeInvalid": "${nextYear - 3}-10-28T06:00:00Z"
        |}""".stripMargin

    val response = Await.result(handler.apply(buildRequest(toJson(body))))

    response.statusCode should be(400)
  }
}
