package com.chargingsystem.server

import com.chargingsystem.core.healthcheck.HealthCheckHandler
import com.chargingsystem.core.invoices.api.InvoiceHandler
import com.chargingsystem.core.overviews.api.OverviewHandler
import com.chargingsystem.core.tariffs.api.TariffHandler
import com.chargingsystem.core.transactions.api.StoreTransactionHandler
import com.twitter.server.TwitterServer

trait Handlers extends ConfigLoader {
  self: TwitterServer =>

  lazy val storeTransactionHandler = new StoreTransactionHandler
  lazy val tariffHandler = new TariffHandler
  lazy val invoiceHandler = new InvoiceHandler
  lazy val overviewHandler = new OverviewHandler
  lazy val heathCheckHandler = new HealthCheckHandler
}

