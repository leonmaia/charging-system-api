package com.newmotion.server

import com.twitter.finagle.Redis

trait DataStore {
  var redisClient = Redis.newRichClient("localhost:6379")
}
