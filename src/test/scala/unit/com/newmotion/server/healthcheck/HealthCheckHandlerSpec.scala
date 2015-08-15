package unit.com.newmotion.server.healthcheck

import com.newmotion.core.healthcheck.HealthCheckHandler
import com.newmotion.server.DataStore
import com.twitter.finagle.http.Request
import com.twitter.util.Await
import org.scalatest.{FlatSpec, Matchers}

class HealthCheckHandlerSpec extends FlatSpec with Matchers with DataStore{

  val handler = new HealthCheckHandler()

  behavior of "#apply"

  it should "return status code 200" in {
    val response = Await.result(handler.apply(Request()))

    response.getContentString should include("It Works! Yay!")
    response.statusCode should be(200)
  }
}
