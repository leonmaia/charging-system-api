package unit.com.newmotion.core.transactions

import com.newmotion.core.transactions.StoreTransactionHandler
import com.newmotion.server.DataStore
import com.newmotion.util.JsonSupport
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.twitter.util.Future
import com.typesafe.config.Config
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.handler.codec.http.HttpMethod
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FlatSpec, Matchers}
import unit.com.newmotion.core.RequestHelper

class ComputeFeeHandlerSpec extends FlatSpec with Matchers with MockitoSugar with JsonSupport with BeforeAndAfter with BeforeAndAfterEach with RequestHelper{
  var config = mock[Config]
  var request: Request = _
  var handler: StoreTransactionHandler = _
  implicit var redis: Client = _

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new StoreTransactionHandler with TestRedisStore
  }

  trait TestRedisStore extends DataStore {
    redisClient = redis
  }
}


