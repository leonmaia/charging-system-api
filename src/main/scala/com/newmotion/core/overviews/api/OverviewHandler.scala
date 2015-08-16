package com.newmotion.core.overviews.api

import com.newmotion.core.tariffs.Tariff
import com.newmotion.core.transactions.Transaction
import com.newmotion.server.RedisStore
import com.newmotion.server.http.Responses._
import com.newmotion.service.tracing.Tracing
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.redis.util.CBToString
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponseStatus

class OverviewHandler extends Service[Request, Response] with Tracing with RedisStore {
  def apply(request: Request): Future[Response] = {
    getSetLen("transactions") flatMap {
      case l if l > 0 =>
        getAllMembers("transactions") flatMap {
          resp =>
            val transactions = resp.map(CBToString(_)).mkString(" ")
            Future(respond(transactions, HttpResponseStatus.OK, contentType = "text/csv"))
        }
      case _ => Future(respond("", HttpResponseStatus.NOT_FOUND))
    }
  }
}
