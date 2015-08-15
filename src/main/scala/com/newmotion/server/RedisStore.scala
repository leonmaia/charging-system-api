package com.newmotion.server

import com.twitter.finagle.redis.util.{CBToString, StringToChannelBuffer}
import com.twitter.util.Future

trait RedisStore extends DataStore {
  def get(key: String): Future[Option[String]] = {
    val k = StringToChannelBuffer(key)
    redisClient.get(k) flatMap {
      case Some(v) => Future(Some(CBToString(v)))
      case _ => Future.value(None)
    }
  }
}

