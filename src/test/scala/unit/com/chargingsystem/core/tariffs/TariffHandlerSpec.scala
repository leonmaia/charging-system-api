package unit.com.chargingsystem.core.tariffs

import com.chargingsystem.core.tariffs.Tariff
import com.chargingsystem.core.tariffs.api.TariffHandler
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.twitter.finagle.redis.util.StringToChannelBuffer
import com.twitter.util.{Await, Future}
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.handler.codec.http.HttpMethod
import org.joda.time.LocalDateTime
import org.mockito.Matchers.any
import org.mockito.Mockito._
import unit.com.chargingsystem.core.BaseSpec

class TariffHandlerSpec extends BaseSpec {
  var handler: TariffHandler = _

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new TariffHandler with TestRedisStore
    val lng = java.lang.Long.valueOf(1L)
    when(redis.sAdd(any[ChannelBuffer], any[List[ChannelBuffer]])).thenReturn(Future(lng))
  }

  behavior of "#apply with valid body"

  it should "return status code 201" in {
    when(redis.sMembers(any[ChannelBuffer])).
      thenReturn(Future(Set(StringToChannelBuffer(s"${nextYear - 1}-10-28T06:00:00Z,something_else"))))

    val tariff = Tariff(0.20, 1.00, 0.25, s"$nextYear-10-28T06:00:00Z")
    val response = Await.result(handler.apply(buildRequest(toJson(tariff), HttpMethod.POST)))

    response.statusCode should be(201)
  }

  it should "insert in redis" in {
    when(redis.sMembers(any[ChannelBuffer])).
      thenReturn(Future(Set(StringToChannelBuffer(s"${nextYear - 1}-10-28T06:00:00Z,something_else"))))

    val tariff = Tariff(0.20, 1.00, 0.25, s"$nextYear-10-28T06:00:00Z")
    Await.result(handler.apply(buildRequest(toJson(tariff), HttpMethod.POST)))

    verify(redis, times(1)).sAdd(any[ChannelBuffer], any[List[ChannelBuffer]])
  }

  behavior of "#apply with invalid body"

  it should "return status code 400" in {
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

  behavior of "#apply breaking activeStarting requirements"

  it should "return status code 400 if activeStarting before now" in {
    val body =
      s"""{
         |"startFee": 0.20,
         |"hourlyFee": 1.00,
         |"feePerKWh": 0.25,
         |"activeStarting": "$nextYear-10-28T06:00:00Z"
                                        |}""".stripMargin
    val response = Await.result(handler.apply(buildRequest(toJson(body), HttpMethod.POST)))

    response.statusCode should be(400)
  }

  it should "return status code 400 if latest activeStarting if later" in {
    when(redis.sMembers(any[ChannelBuffer])).
      thenReturn(Future(Set(StringToChannelBuffer(s"${nextYear + 1}-10-28T06:00:00Z,something_else"))))

    val tariff = Tariff(0.20, 1.00, 0.25, s"$nextYear-10-28T06:00:00Z")
    val response = Await.result(handler.apply(buildRequest(toJson(tariff), HttpMethod.POST)))

    response.statusCode should be(400)
  }

  behavior of "#extractFirstField"
  it should "represent activeStarting string" in {
    handler.extractActiveStarting(s"$nextYear-10-28T06:00:00Z,more_data") should be(s"$nextYear-10-28T06:00:00Z")
  }

  behavior of "#isValid"
  it should "return false if later date is stored in redis" in {
    when(redis.sMembers(any[ChannelBuffer])).
      thenReturn(Future(Set(StringToChannelBuffer(s"${nextYear + 1}-10-28T06:00:00Z,something_else"))))
    val result = Await.result(handler.isValid(s"$nextYear-10-28T06:00:00Z"))

    result should be(false)
  }

  it should "return true if no later date is stored in redis" in {
    when(redis.sMembers(any[ChannelBuffer])).
      thenReturn(Future(Set(StringToChannelBuffer(s"${nextYear - 1}-10-28T06:00:00Z,something_else"))))
    val result = Await.result(handler.isValid(s"$nextYear-10-28T06:00:00Z"))

    result should be(true)
  }
}
