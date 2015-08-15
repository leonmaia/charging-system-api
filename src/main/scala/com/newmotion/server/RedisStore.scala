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

  def set(key: String, value: String): Future[Unit] = {
    val k = StringToChannelBuffer(key)
    val v = StringToChannelBuffer(value)
    redisClient.set(k, v)
  }

  def addSet(key: String, member: String): Future[Long] = {
    val k = StringToChannelBuffer(key)
    val v = StringToChannelBuffer(member)
    redisClient.sAdd(k, v :: Nil).map(_.toLong)
  }

  def incr(key: String) = {
    val k = StringToChannelBuffer(key)
    redisClient.incr(k).map(_.toLong)
  }
}

