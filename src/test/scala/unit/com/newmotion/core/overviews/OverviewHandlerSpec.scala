package unit.com.newmotion.core.overviews

import com.newmotion.core.overviews.api.OverviewHandler
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

class OverviewHandlerSpec extends BaseSpec {
  var handler: OverviewHandler = _

  val transaction = new Transaction(id = "pete", startTime = s"$nextYear-10-27T13:32:14Z",
    endTime = s"$nextYear-10-27T14:32:14Z", volume = 13.21)
  val tKey = s"${transaction.id},${transaction.startTime},${transaction.endTime},${transaction.volume}"
  val tKey1 = s"${transaction.id}eleon,${transaction.startTime},${transaction.endTime},${transaction.volume}"
  val tariff = Tariff(0.20, 1.00, 0.25, s"$nextYear-09-28T06:00:00Z")
  val tariffKey = s"${tariff.activeStarting},${tariff.startFee},${tariff.hourlyFee},${tariff.feePerKWh}"

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new OverviewHandler with TestRedisStore
  }

  behavior of "#apply with resources"

  it should "respond with content type text/csv" in {
    when(redis.sCard(any[ChannelBuffer])).thenReturn(Future(java.lang.Long.valueOf(1L)))
    when(redis.sMembers(StringToChannelBuffer("transactions"))).thenReturn(Future(Set(StringToChannelBuffer(tKey))))
    when(redis.sMembers(StringToChannelBuffer("tariffs"))).thenReturn(Future(Set.empty[ChannelBuffer]))
    val response = Await.result(handler.apply(buildRequest()))

    response.contentType.get should be("text/csv;charset=utf-8")
  }

  it should "return status code 200" in {
    when(redis.sCard(any[ChannelBuffer])).thenReturn(Future(java.lang.Long.valueOf(1L)))
    when(redis.sMembers(StringToChannelBuffer("transactions"))).thenReturn(Future(Set(StringToChannelBuffer(tKey))))
    when(redis.sMembers(StringToChannelBuffer("tariffs"))).thenReturn(Future(Set.empty[ChannelBuffer]))
    val response = Await.result(handler.apply(buildRequest()))

    response.statusCode should be(200)
  }

  it should "show correctly the content without tariff" in {
    when(redis.sCard(any[ChannelBuffer])).thenReturn(Future(java.lang.Long.valueOf(2L)))
    val list = Set(StringToChannelBuffer(tKey), StringToChannelBuffer(tKey1))
    when(redis.sMembers(StringToChannelBuffer("tariffs"))).thenReturn(Future(Set.empty[ChannelBuffer]))
    when(redis.sMembers(StringToChannelBuffer("transactions"))).thenReturn(Future(list))
    val response = Await.result(handler.apply(buildRequest()))

    response.statusCode should be(200)
    response.getContentString should be(s"pete,$nextYear-10-27T13:32:14Z,$nextYear-10-27T14:32:14Z,13.21 peteeleon,$nextYear-10-27T13:32:14Z,$nextYear-10-27T14:32:14Z,13.21")
  }

  it should "show correctly the content with tariff" in {
    when(redis.sCard(any[ChannelBuffer])).thenReturn(Future(java.lang.Long.valueOf(2L)))
    val list = Set(StringToChannelBuffer(tKey), StringToChannelBuffer(tKey1))
    when(redis.sMembers(StringToChannelBuffer("tariffs"))).thenReturn(Future(Set(StringToChannelBuffer(tariffKey))))
    when(redis.sMembers(StringToChannelBuffer("transactions"))).thenReturn(Future(list))
    val response = Await.result(handler.apply(buildRequest()))

    response.statusCode should be(200)
    response.getContentString should be(s"pete,$nextYear-10-27T13:32:14Z,$nextYear-10-27T14:32:14Z,13.21,4.50 peteeleon,$nextYear-10-27T13:32:14Z,$nextYear-10-27T14:32:14Z,13.21,4.50")
  }

  behavior of "#apply without resources"

  it should "return status code 404 if there is no resources" in {
    when(redis.sCard(any[ChannelBuffer])).thenReturn(Future(java.lang.Long.valueOf(0L)))
    val response = Await.result(handler.apply(buildRequest()))

    response.statusCode should be(404)
  }
}
