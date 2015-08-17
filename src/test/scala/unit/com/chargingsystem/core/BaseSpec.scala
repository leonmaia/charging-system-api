package unit.com.chargingsystem.core

import com.chargingsystem.server.DataStore
import com.chargingsystem.util.JsonSupport
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.typesafe.config.Config
import org.jboss.netty.handler.codec.http.HttpMethod
import org.joda.time.LocalDateTime
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FlatSpec, Matchers}

trait BaseSpec extends FlatSpec with Matchers with MockitoSugar with JsonSupport with BeforeAndAfter with BeforeAndAfterEach {
  val nextYear = LocalDateTime.now().getYear + 1
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
