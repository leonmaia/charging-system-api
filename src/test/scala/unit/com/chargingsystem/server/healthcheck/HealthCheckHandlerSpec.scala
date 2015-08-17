package unit.com.chargingsystem.server.healthcheck

import com.chargingsystem.core.healthcheck.HealthCheckHandler
import com.chargingsystem.server.DataStore
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
