package com.chargingsystem.core.invoices.api

import com.chargingsystem.core.invoices.Invoice
import com.chargingsystem.core.overviews.TransactionFees
import com.chargingsystem.core.transactions.TransactionFilter._
import com.chargingsystem.core.transactions.Filter
import com.chargingsystem.server.http.Responses._
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponseStatus._

class InvoiceHandler extends TransactionFees {

  def show(year: String, month: String, customerName: String) = new Service[Request, Response] {
    def createFilters: List[Filter] = {
      Filter("year", year) :: Filter("month", month) ::
        Filter("customer_name", customerName) :: Nil
    }

    def apply(request: Request): Future[Response] = {
      getSetLen("transactions") flatMap {
        case l if l > 0 =>
          requestTransactions map (o => Invoice(filter(o.filter(_.total.isDefined), createFilters)) match {
            case Some(i) => respond(i.template, OK, contentType = "text/txt")
            case _ => respond("", NOT_FOUND)
          })
        case _ =>
          Future(respond(s"Errors!", NOT_FOUND))
      }
    }
  }
}


