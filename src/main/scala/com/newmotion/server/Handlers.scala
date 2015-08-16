package com.newmotion.server

import com.newmotion.core.healthcheck.HealthCheckHandler
import com.newmotion.core.overviews.api.OverviewHandler
import com.newmotion.core.transactions.api.StoreTransactionHandler
import com.twitter.server.TwitterServer

trait Handlers extends ConfigLoader {
  self: TwitterServer =>

  lazy val storeTransactionHandler = new StoreTransactionHandler
  lazy val overviewHandler = new OverviewHandler
  lazy val heathCheckHandler = new HealthCheckHandler
}

