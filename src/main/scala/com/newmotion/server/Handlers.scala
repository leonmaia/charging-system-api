package com.newmotion.server

import com.newmotion.core.healthcheck.HealthCheckHandler
import com.twitter.server.TwitterServer

trait Handlers extends ConfigLoader {
  self: TwitterServer =>

  lazy val heathCheckHandler = new HealthCheckHandler
}

