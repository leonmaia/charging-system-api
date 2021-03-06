package com.chargingsystem.core.overviews.api

import com.chargingsystem.core.overviews.{Overview, TransactionFees}
import com.chargingsystem.server.http.Responses._
import com.chargingsystem.service.tracing.Tracing
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponseStatus._

class OverviewHandler extends Service[Request, Response] with Tracing with TransactionFees {
  def apply(request: Request): Future[Response] = {
    getSetLen("transactions") flatMap {
      case length if length > 0 =>
        requestTransactions map ( transactions => respond(Overview(transactions).asCSV, OK, contentType = "text/csv"))
      case _ => Future(respond("", NOT_FOUND))
    }
  }
}

