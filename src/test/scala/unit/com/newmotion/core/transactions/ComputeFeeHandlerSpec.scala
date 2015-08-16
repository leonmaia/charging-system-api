package unit.com.newmotion.core.transactions

import com.newmotion.core.transactions.StoreTransactionHandler
import com.newmotion.server.DataStore
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import unit.com.newmotion.core.BaseSpec

class ComputeFeeHandlerSpec extends BaseSpec {
  var handler: StoreTransactionHandler = _

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new StoreTransactionHandler with TestRedisStore
  }

  behavior of "#apply"

  it should "parse correctly" in {
    val body =
      """{
        |"startFee": 0.20,
        |"hourlyFee": 1.00,
        |"feePerKWh": 0.25,
        |"activeStarting": "2014-10-28T06:00:00Z"
        |}""".stripMargin
    val fee = fromJson[Fee](body)

    fee.startFee should be(0.20)
    fee.hourlyFee should be(1.00)
    fee.feePerKWh should be(0.25)
    fee.activeStarting should be("2014-10-28T06:00:00Z")
  }

  trait TestRedisStore extends DataStore {
    redisClient = redis
  }
}

case class Fee(startFee: Double, hourlyFee: Double, feePerKWh: Double, activeStarting: String)
