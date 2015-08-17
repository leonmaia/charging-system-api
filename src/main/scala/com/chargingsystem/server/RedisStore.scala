package com.chargingsystem.server

import com.twitter.finagle.redis.util.{CBToString, StringToChannelBuffer}
import com.twitter.util.Future
import org.jboss.netty.buffer.ChannelBuffer

trait RedisStore extends DataStore {
  def get(key: String): Future[Option[String]] = {
    val k = StringToChannelBuffer(key)
    redisClient.get(k) flatMap {
      case Some(v) => Future(Some(CBToString(v)))
      case _ => Future.value(None)
    }
  }

  def addSet(key: String, member: String): Future[Long] = {
    val k = StringToChannelBuffer(key)
    val v = StringToChannelBuffer(member)
    redisClient.sAdd(k, v :: Nil).map(_.toLong)
  }

  def getSetLen(key: String): Future[Long] = {
    redisClient.sCard(StringToChannelBuffer(key)).map(_.toLong)
  }

  def getAllMembers(member: String): Future[List[ChannelBuffer]] = {
    redisClient.sMembers(StringToChannelBuffer(member)).map(_.toList)
  }

  def incr(key: String) = {
    val k = StringToChannelBuffer(key)
    redisClient.incr(k).map(_.toLong)
  }
}

