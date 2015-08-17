package com.newmotion.core.tariffs.api

import com.newmotion.core.tariffs.Tariff
import com.newmotion.server.RedisStore
import com.newmotion.server.http.Responses._
import com.newmotion.service.tracing.Tracing
import com.newmotion.util.DateSupport
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.redis.util.CBToString
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponseStatus._

import scala.util.{Failure, Success, Try}

class TariffHandler extends Service[Request, Response] with Tracing with RedisStore {
  override def apply(request: Request): Future[Response] = {
    Try(Tariff(request)) match {
      case Success(t) =>
        isValid(t.activeStarting) map {
          case x if x =>
            addSet("tariffs", t.createCSVKey)
            respond("", CREATED)
          case x if !x => respond("", BAD_REQUEST)
        }
      case Failure(f) => Future(respond("Errors!", BAD_REQUEST))
    }
  }

  def extractFirstField(v: String) = v.substring(0, v.indexOf(","))

  // todo remove this when sscan is implemented by finagle-redis
  def isValid(value: String): Future[Boolean] = {
    val ds = new DateSupport
    val date = ds.parse(value)
    getAllMembers("tariffs") map {
      resp =>
        val total = resp.size
        val validDates = resp.takeWhile(r => ds.parse(extractFirstField(CBToString(r))).isBefore(date)).size
        if (total > validDates) false else true
    }
  }
}
