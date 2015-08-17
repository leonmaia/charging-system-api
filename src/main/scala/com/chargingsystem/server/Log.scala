package com.chargingsystem.server

import com.twitter.logging.Logger

trait Log {
  val log = Logger.get(getClass)
}
