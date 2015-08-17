package com.chargingsystem.server

import com.chargingsystem.server.http.HttpRouter
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.Method
import com.twitter.server.TwitterServer

trait Router extends Handlers {
  self: TwitterServer =>

  lazy val router = HttpRouter.forRoutes({
    case Method.Get ->  Root / "healthcheck" => heathCheckHandler
    case Method.Get ->  Root / "overview" => overviewHandler
    case Method.Post ->  Root / "transactions" => storeTransactionHandler
    case Method.Post ->  Root / "tariffs" => tariffHandler
    case Method.Get ->  Root / "invoices" / year / month / customerName => invoiceHandler.show(year, month, customerName)
  })
}
