package unit.com.newmotion.core

import com.newmotion.server.DataStore
import com.newmotion.util.JsonSupport
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.typesafe.config.Config
import org.jboss.netty.handler.codec.http.HttpMethod
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FlatSpec, Matchers}

trait BaseSpec extends FlatSpec with Matchers with MockitoSugar with JsonSupport with BeforeAndAfter with BeforeAndAfterEach {
  var config = mock[Config]
  var request: Request = _
  implicit var redis: Client = _

  def buildRequest(content: String = "", method: HttpMethod = HttpMethod.GET) = {
    val req = Request()
    req.setContentTypeJson()
    req.setContentString(content)
    req.setMethod(method)

    req
  }

  trait TestRedisStore extends DataStore {
    redisClient = redis
  }
}
