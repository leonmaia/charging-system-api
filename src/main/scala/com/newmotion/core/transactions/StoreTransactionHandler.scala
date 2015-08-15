package com.newmotion.core.transactions

import com.newmotion.models.Transaction
import com.newmotion.server.RedisStore
import com.newmotion.server.http.Responses._
import com.newmotion.service.tracing.Tracing
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponseStatus

import scala.util.{Failure, Success, Try}

class StoreTransactionHandler extends Tracing with RedisStore {

  def apply(request: Request): Future[Response] = {
    Try(Transaction(request)) match {
      case Success(t) => Future(respond("", HttpResponseStatus.CREATED))
      case Failure(f) => Future(respond("Errors!", HttpResponseStatus.BAD_REQUEST))
    }
  }
}
