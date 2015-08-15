package unit.com.newmotion.core.transactions

import com.newmotion.core.transactions.StoreTransactionHandler
import com.newmotion.models.Transaction
import com.newmotion.server.DataStore
import com.newmotion.util.JsonSupport
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import com.twitter.util.{Await, Future}
import com.typesafe.config.Config
import org.mockito.Matchers.{any, anyString}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FlatSpec, Matchers}

class StoreTransactionHandlerSpec extends FlatSpec with Matchers with MockitoSugar with JsonSupport with BeforeAndAfter with BeforeAndAfterEach{
  var config = mock[Config]
  var request: Request = _
  var handler: StoreTransactionHandler = _
  implicit var redis: Client = _

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new StoreTransactionHandler with TestRedisStore
  }

  def buildRequest(content: String) = {
    val req = Request()
    req.setContentString(content)
    req.setContentTypeJson()

    req
  }

  behavior of "#apply"

  it should "return status code 201" in {
    val req = new Transaction(id = "pete", startTime = "2014-10-27T13:32:14Z",
      endTime = "2014-10-27T14:32:14Z", volume = 13.21)
    val response = Await.result(handler.apply(buildRequest(toJson(req))))

    response.statusCode should be(201)
  }

  it should "return status code 400 if error" in {
    val body =
      """
        |{
        |"customerId": "john",
        |"startTime": "2014-10-28T09:34:17Z",
        |"invalidField": "2014-10-28T16:45:13Z",
        |"volume": 32.03
        |}""".stripMargin

    val response = Await.result(handler.apply(buildRequest(toJson(body))))

    response.statusCode should be(400)
  }

  trait TestRedisStore extends DataStore {
    redisClient = redis
  }
}

