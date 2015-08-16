package com.newmotion.core.overviews.api

import com.newmotion.core.overviews.Overview
import com.newmotion.server.RedisStore
import com.newmotion.server.http.Responses._
import com.newmotion.service.tracing.Tracing
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.redis.util.CBToString
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponseStatus._

class OverviewHandler extends Service[Request, Response] with Tracing with RedisStore {
  def apply(request: Request): Future[Response] = {
    getSetLen("transactions") flatMap {
      case l if l > 0 =>
        getAllMembers("transactions") flatMap {
          transactions =>
            getAllMembers("tariffs") map { tariffs =>
              if (tariffs.isEmpty) {
                respond(Overview(transactions.map(CBToString(_))).value, OK, contentType = "text/csv")
              } else {
                val csv = Overview(transactions.map(CBToString(_)), tariffs.map(CBToString(_))).value
                respond(csv, OK, contentType = "text/csv")
              }
            }
        }
      case _ => Future(respond("", NOT_FOUND))
    }
  }
}
