package unit.com.newmotion.core.transactions


import com.newmotion.core.transactions.StoreTransactionHandler
import com.newmotion.models.Transaction
import com.newmotion.server.DataStore
import com.newmotion.util.JsonSupport
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.twitter.finagle.redis.util.StringToChannelBuffer
import com.twitter.util.{Await, Future}
import com.typesafe.config.Config
import org.jboss.netty.buffer.ChannelBuffer
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FlatSpec, Matchers}
import com.newmotion.core.transactions.OverviewHandler
import com.newmotion.server.DataStore
import com.newmotion.util.JsonSupport
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.twitter.util.Await
import com.typesafe.config.Config
import org.jboss.netty.handler.codec.http.HttpMethod
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FlatSpec, Matchers}

class OverviewHandlerSpec extends FlatSpec with Matchers with MockitoSugar with JsonSupport with BeforeAndAfter with BeforeAndAfterEach{
  var config = mock[Config]
  var request: Request = _
  var handler: OverviewHandler = _
  implicit var redis: Client = _

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new OverviewHandler with TestRedisStore
  }

  def buildRequest(method: HttpMethod = HttpMethod.GET) = {
    val req = Request()
    req.setContentTypeJson()
    req.setMethod(method)

    req
  }

  def buildRequest(content: String) = {
    val req = Request()
    req.setContentString(content)
    req.setContentTypeJson()

    req
  }

  behavior of "#apply"

  it should "return status code 404 if there is no resources" in {
    when(redis.sCard(any[ChannelBuffer])).thenReturn(Future(java.lang.Long.valueOf(0L)))
    val response = Await.result(handler.apply(buildRequest()))

    response.statusCode should be(404)
  }

  it should "respond with content type text/csv" in {
    when(redis.sCard(any[ChannelBuffer])).thenReturn(Future(java.lang.Long.valueOf(1L)))
    when(redis.sMembers(any[ChannelBuffer])).thenReturn(Future(Set(StringToChannelBuffer("alguma_coisa"))))
    val response = Await.result(handler.apply(buildRequest()))

    response.contentType.get should be("text/csv;charset=utf-8")
  }

  it should "return status code 200 if resource exists" in {
    when(redis.sCard(any[ChannelBuffer])).thenReturn(Future(java.lang.Long.valueOf(1L)))
    when(redis.sMembers(any[ChannelBuffer])).thenReturn(Future(Set(StringToChannelBuffer("alguma_coisa"))))
    val response = Await.result(handler.apply(buildRequest()))

    response.statusCode should be(200)
  }

  trait TestRedisStore extends DataStore {
    redisClient = redis
  }
}
