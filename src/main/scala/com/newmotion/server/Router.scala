package com.newmotion.server

import com.newmotion.server.http.HttpRouter
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.Method
import com.twitter.server.TwitterServer

trait Router extends Handlers {
  self: TwitterServer =>

  lazy val router = HttpRouter.forRoutes({
    case Method.Get ->  Root / "healthcheck" => heathCheckHandler
    case Method.Get ->  Root / "overview" => overviewHandler
    case Method.Post ->  Root / "transactions" => storeTransactionHandler
  })
}
