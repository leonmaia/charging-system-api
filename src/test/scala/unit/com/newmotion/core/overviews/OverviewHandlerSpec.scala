package unit.com.newmotion.core.overviews

import com.newmotion.core.transactions.OverviewHandler
import com.newmotion.server.DataStore
import com.newmotion.util.JsonSupport
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.twitter.finagle.redis.util.StringToChannelBuffer
import com.twitter.util.{Await, Future}
import com.typesafe.config.Config
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.handler.codec.http.HttpMethod
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FlatSpec, Matchers}
import unit.com.newmotion.core.RequestHelper

class OverviewHandlerSpec extends FlatSpec with Matchers with MockitoSugar with JsonSupport with BeforeAndAfter with BeforeAndAfterEach with RequestHelper {
  var config = mock[Config]
  var request: Request = _
  var handler: OverviewHandler = _
  implicit var redis: Client = _

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new OverviewHandler with TestRedisStore
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

  it should "show correctly the content" in {
    when(redis.sCard(any[ChannelBuffer])).thenReturn(Future(java.lang.Long.valueOf(2L)))
    val list = Set(StringToChannelBuffer("alguma_coisa"), StringToChannelBuffer("outra_coisa"))
    when(redis.sMembers(any[ChannelBuffer])).thenReturn(Future(list))
    val response = Await.result(handler.apply(buildRequest()))

    response.statusCode should be(200)
    response.getContentString should be("alguma_coisa outra_coisa")
  }

  trait TestRedisStore extends DataStore {
    redisClient = redis
  }
}
