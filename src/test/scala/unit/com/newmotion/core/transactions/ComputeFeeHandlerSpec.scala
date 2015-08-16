package unit.com.newmotion.core.transactions

import com.newmotion.core.transactions.ComputeFeeHandler
import com.newmotion.models.Fee
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.twitter.finagle.redis.util.StringToChannelBuffer
import com.twitter.util.{Await, Future}
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.handler.codec.http.HttpMethod
import org.joda.time.LocalDateTime
import org.mockito.Matchers.any
import org.mockito.Mockito._
import unit.com.newmotion.core.BaseSpec

class ComputeFeeHandlerSpec extends BaseSpec {
  var handler: ComputeFeeHandler = _
  val nextYear = LocalDateTime.now().getYear + 1

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new ComputeFeeHandler with TestRedisStore
    val lng = java.lang.Long.valueOf(1L)
    when(redis.sAdd(any[ChannelBuffer], any[List[ChannelBuffer]])).thenReturn(Future(lng))
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

  it should "return status code 201" in {
    when(redis.sMembers(any[ChannelBuffer])).
      thenReturn(Future(Set(StringToChannelBuffer(s"${nextYear - 1}-10-28T06:00:00Z,something_else"))))

    val fee = Fee(0.20, 1.00, 0.25, s"$nextYear-10-28T06:00:00Z")
    val response = Await.result(handler.apply(buildRequest(toJson(fee), HttpMethod.POST)))

    response.statusCode should be(201)
  }

  it should "insert in redis" in {
    when(redis.sMembers(any[ChannelBuffer])).
      thenReturn(Future(Set(StringToChannelBuffer(s"${nextYear - 1}-10-28T06:00:00Z,something_else"))))

    val fee = Fee(0.20, 1.00, 0.25, s"$nextYear-10-28T06:00:00Z")
    Await.result(handler.apply(buildRequest(toJson(fee), HttpMethod.POST)))

    verify(redis, times(1)).sAdd(any[ChannelBuffer], any[List[ChannelBuffer]])
  }

  it should "return status code 400 if error" in {
    val body =
      s"""{
        |"startFee": 0.20,
        |"hourlyFee": 1.00,
        |"feePerKWh": 0.25,
        |"activeInvalid": "$nextYear-10-28T06:00:00Z"
        |}""".stripMargin

    val response = Await.result(handler.apply(buildRequest(toJson(body), HttpMethod.POST)))

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

    val response = Await.result(handler.apply(buildRequest(toJson(body), HttpMethod.POST)))

    response.statusCode should be(400)
  }

  it should "return status code 400 if latest activeStarting if later" in {
    when(redis.sMembers(any[ChannelBuffer])).
      thenReturn(Future(Set(StringToChannelBuffer(s"$nextYear-10-28T06:00:00Z,something_else"))))
    val body =
      s"""{
         |"startFee": 0.20,
         |"hourlyFee": 1.00,
         |"feePerKWh": 0.25,
         |"activeInvalid": "${nextYear - 1}-10-28T06:00:00Z"
         |}""".stripMargin

    val response = Await.result(handler.apply(buildRequest(toJson(body), HttpMethod.POST)))

    response.statusCode should be(400)
  }
}
