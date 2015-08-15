package com.newmotion.server

import com.twitter.logging.Logger

trait Log {
  val log = Logger.get(getClass)
}
