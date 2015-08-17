package com.newmotion.core.overviews.api

import com.newmotion.core.overviews.{Overview, TransactionFees}
import com.newmotion.server.http.Responses._
import com.newmotion.service.tracing.Tracing
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponseStatus._

class OverviewHandler extends Service[Request, Response] with Tracing with TransactionFees {
  def apply(request: Request): Future[Response] = {
    getSetLen("transactions") flatMap {
      case l if l > 0 =>
        build map ( t => respond(Overview(t).asCSV, OK, contentType = "text/csv"))
      case _ => Future(respond("", NOT_FOUND))
    }
  }
}

