package unit.com.newmotion.core.invoices

import com.newmotion.core.invoices.api.InvoiceHandler
import com.newmotion.core.tariffs.Tariff
import com.newmotion.core.transactions.Transaction
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.twitter.finagle.redis.util.StringToChannelBuffer
import com.twitter.util.{Await, Future}
import org.jboss.netty.buffer.ChannelBuffer
import org.mockito.Matchers.any
import org.mockito.Mockito._
import unit.com.newmotion.core.BaseSpec

class InvoiceHandlerSpec extends BaseSpec {
  var handler: InvoiceHandler = _

  val t0 = new Transaction(id = "pete", startTime = s"$nextYear-08-10T13:32:14Z",
    endTime = s"$nextYear-08-10T19:32:14Z", volume = 13.21).createCSVKey
  val t1 = new Transaction(id = "pete", startTime = s"$nextYear-08-27T13:32:14Z",
    endTime = s"$nextYear-08-27T14:32:14Z", volume = 13.21).createCSVKey
  val t2 = new Transaction(id = "pete", startTime = s"$nextYear-08-27T13:32:14Z",
    endTime = s"$nextYear-08-27T14:32:14Z", volume = 13.21).createCSVKey
  val tariff = Tariff(0.20, 1.00, 0.25, s"$nextYear-08-26T06:00:00Z").createCSVKey

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new InvoiceHandler with TestRedisStore
    when(redis.sCard(any[ChannelBuffer])).thenReturn(Future(java.lang.Long.valueOf(2L)))
    when(redis.sMembers(StringToChannelBuffer("transactions"))).thenReturn(Future(Set(StringToChannelBuffer(t0), StringToChannelBuffer(t1))))
    when(redis.sMembers(StringToChannelBuffer("tariffs"))).thenReturn(Future(Set(StringToChannelBuffer(tariff))))
  }

  behavior of "#show"
  it should "return status code 200" in {
    val response = Await.result(handler.show(year = s"$nextYear", month = "08", "pete")(Request()))

    response.statusCode should be(200)
  }

  it should "respond with content type text/txt" in {
    val response = Await.result(handler.show(s"$nextYear", month = "08", "pete")(Request()))

    response.statusCode should be(200)
    response.contentType.get should be("text/txt;charset=utf-8")
  }

  behavior of "#show without transactions"
  it should "return status code 404" in {
    when(redis.sCard(any[ChannelBuffer])).thenReturn(Future(java.lang.Long.valueOf(0L)))
    val response = Await.result(handler.show(year = s"$nextYear", month = "08", "pete")(Request()))

    response.statusCode should be(404)
  }

  it should "return status code 404 if filtered is empty" in {
    val response = Await.result(handler.show(year = "2014", month = "08", "pete")(Request()))

    response.statusCode should be(404)
  }

  behavior of "#show#createFilters"
  it should "return a list of filters" in {
    val filters = handler.show("2014", month = "08", "pete").createFilters

    filters.size should be(3)
  }

}
