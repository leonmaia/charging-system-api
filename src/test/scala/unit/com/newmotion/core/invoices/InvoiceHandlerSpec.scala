package unit.com.newmotion.core.invoices

import com.newmotion.core.invoices.api.InvoiceHandler
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.twitter.util.Await
import unit.com.newmotion.core.BaseSpec

class InvoiceHandlerSpec extends BaseSpec {
  var handler: InvoiceHandler = _

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new InvoiceHandler with TestRedisStore
  }

  behavior of "#show"
  it should "return status code 200" in {
    val response = Await.result(handler.show("2014", month = "08", "pete")(Request()))

    response.statusCode should be(200)
  }

  it should "respond with content type text/txt" in {
    val response = Await.result(handler.show("2014", month = "08", "pete")(Request()))

    response.statusCode should be(200)
    response.contentType.get should be("text/txt;charset=utf-8")
  }

  behavior of "#show#createFilters"
  it should "return a list of filters" in {
    val filters = handler.show("2014", month = "08", "pete").createFilters

    filters.size should be(3)
  }
}
