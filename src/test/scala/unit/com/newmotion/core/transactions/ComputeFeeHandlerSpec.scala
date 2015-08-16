package unit.com.newmotion.core.transactions

import com.newmotion.core.transactions.StoreTransactionHandler
import com.newmotion.server.DataStore
import com.twitter.finagle.http.Request
import com.twitter.finagle.redis.Client
import unit.com.newmotion.core.BaseSpec

class ComputeFeeHandlerSpec extends BaseSpec {
  var handler: StoreTransactionHandler = _

  before {
    request = mock[Request]
    redis = mock[Client]
    handler = new StoreTransactionHandler with TestRedisStore
  }

  trait TestRedisStore extends DataStore {
    redisClient = redis
  }
}


