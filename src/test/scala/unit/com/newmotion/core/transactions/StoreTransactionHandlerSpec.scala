package unit.com.newmotion.core.transactions

import com.newmotion.core.transactions.StoreTransactionHandler
import com.newmotion.models.Transaction
import com.newmotion.models.Transaction
import com.newmotion.server.DataStore
import com.newmotion.util.JsonSupport
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.twitter.util.{Await, Future}
import com.typesafe.config.Config
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.handler.codec.http.HttpMethod
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FlatSpec, Matchers}
import unit.com.newmotion.core.RequestHelper

class StoreTransactionHandlerSpec extends FlatSpec with Matchers with MockitoSugar with JsonSupport with BeforeAndAfter with BeforeAndAfterEach with RequestHelper{
  var config = mock[Config]
  var request: Request = _
  var handler: StoreTransactionHandler = _
  implicit var redis: Client = _

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new StoreTransactionHandler with TestRedisStore
    val lng = java.lang.Long.valueOf(1L)
    when(redis.sAdd(any[ChannelBuffer], any[List[ChannelBuffer]])).thenReturn(Future(lng))
  }

  behavior of "#apply"

  it should "return status code 201" in {
    val req = new Transaction(id = "pete", startTime = "2014-10-27T13:32:14Z",
      endTime = "2014-10-27T14:32:14Z", volume = 13.21)
    val response = Await.result(handler.apply(buildRequest(toJson(req), HttpMethod.POST)))

    response.statusCode should be(201)
  }

  it should "parse correctly" in {
    val body =
      """
        |{
        |"customerId": "john",
        |"startTime": "2014-10-28T09:34:17Z",
        |"endTime": "2014-10-28T16:45:13Z",
        |"volume": 32.03
        |}""".stripMargin
    val transaction = fromJson[Transaction](body)

    transaction.id should be("john")
    transaction.startTime should be("2014-10-28T09:34:17Z")
    transaction.endTime should be("2014-10-28T16:45:13Z")
    transaction.volume should be(32.03)
  }

  it should "insert in redis" in {
    val req = new Transaction(id = "pete", startTime = "2014-10-27T13:32:14Z",
      endTime = "2014-10-27T14:32:14Z", volume = 13.21)
    Await.result(handler.apply(buildRequest(toJson(req))))
    verify(redis, times(1)).sAdd(any[ChannelBuffer], any[List[ChannelBuffer]])
  }

  it should "return status code 400 if error" in {
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

  trait TestRedisStore extends DataStore {
    redisClient = redis
  }
}

