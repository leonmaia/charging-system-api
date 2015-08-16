package com.newmotion.core.transactions

import com.newmotion.server.RedisStore
import com.newmotion.server.http.Responses._
import com.newmotion.service.tracing.Tracing
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.redis.util.CBToString
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import com.twitter.finagle.Service

class OverviewHandler extends Service[Request, Response] with Tracing with RedisStore {
  def apply(request: Request): Future[Response] = {
    getSetLen("transactions") map {
      case l if l > 0 =>
        val list = getAllMembers("transactions") map {
          resp => resp.map(CBToString(_)).mkString(" ")
        }
        respond(list, HttpResponseStatus.OK, contentType = "text/csv")
      case _ => respond("", HttpResponseStatus.NOT_FOUND)
    }
  }
}
