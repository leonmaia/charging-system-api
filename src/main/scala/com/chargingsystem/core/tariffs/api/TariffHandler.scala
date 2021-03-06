package com.chargingsystem.core.tariffs.api

import com.chargingsystem.core.tariffs.Tariff
import com.chargingsystem.server.RedisStore
import com.chargingsystem.server.http.Responses._
import com.chargingsystem.service.tracing.Tracing
import com.chargingsystem.util.DateSupport
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

  def extractActiveStarting(v: String) = v.substring(0, v.indexOf(","))

  // todo remove this when sscan is implemented by finagle-redis
  def isValid(activeStarting: String): Future[Boolean] = {
    val ds = new DateSupport
    val activeStartingDate = ds.parse(activeStarting)
    getAllMembers("tariffs") map {
      tariffs =>
        val total = tariffs.size
        val validDates = tariffs.takeWhile(tariffKey => ds.parse(extractActiveStarting(CBToString(tariffKey))).isBefore(activeStartingDate)).size
        if (total > validDates) false else true
    }
  }
}
