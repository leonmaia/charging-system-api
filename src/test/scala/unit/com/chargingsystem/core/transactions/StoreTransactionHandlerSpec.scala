package unit.com.chargingsystem.core.transactions

import com.chargingsystem.core.transactions.Transaction
import com.chargingsystem.core.transactions.api.StoreTransactionHandler
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.twitter.util.{Await, Future}
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.handler.codec.http.HttpMethod
import org.mockito.Matchers.any
import org.mockito.Mockito._
import unit.com.chargingsystem.core.BaseSpec

class StoreTransactionHandlerSpec extends BaseSpec {

  var handler: StoreTransactionHandler = _

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new StoreTransactionHandler with TestRedisStore
    val lng = java.lang.Long.valueOf(1L)
    when(redis.sAdd(any[ChannelBuffer], any[List[ChannelBuffer]])).thenReturn(Future(lng))
  }

  behavior of "#apply with valid body"

  it should "return status code 201" in {
    val req = new Transaction(id = "pete", startTime = "2014-10-27T13:32:14Z",
      endTime = "2014-10-27T14:32:14Z", volume = 13.21)
    val response = Await.result(handler.apply(buildRequest(toJson(req), HttpMethod.POST)))

    response.statusCode should be(201)
  }

  it should "insert in redis" in {
    val req = Transaction(id = "pete", startTime = "2014-10-27T13:32:14Z",
      endTime = "2014-10-27T14:32:14Z", volume = 13.21)
    Await.result(handler.apply(buildRequest(toJson(req))))
    verify(redis, times(1)).sAdd(any[ChannelBuffer], any[List[ChannelBuffer]])
  }

  behavior of "#apply with invalid body"

  it should "return status code 400" in {
    val body =
      """
        |{
        |"customerId": "john",
        |"startTime": "2014-10-28T09:34:17Z",
        |"invalidField": "2014-10-28T16:45:13Z",
        |"volume": 32.03
        |}""".stripMargin

    val response = Await.result(handler.apply(buildRequest(toJson(body))))

    response.statusCode should be(400)
  }
}

