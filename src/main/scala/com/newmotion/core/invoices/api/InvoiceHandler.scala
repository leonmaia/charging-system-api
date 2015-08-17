package com.newmotion.core.invoices.api

import com.newmotion.core.transactions.Filter
import com.newmotion.server.RedisStore
import com.newmotion.server.http.Responses._
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponseStatus._

class InvoiceHandler extends RedisStore {

  def show(year: String, month: String, customerName: String) = new Service[Request, Response] {
    def createFilters: List[Filter] = {
      Filter("year", year) :: Filter("month", month) ::
        Filter("customer_name", customerName) :: Nil
    }

    def apply(request: Request): Future[Response] = {
      Future(respond(s"Errors!", OK, contentType = "text/txt"))
    }
  }
}


