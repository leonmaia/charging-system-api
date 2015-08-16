package com.newmotion.core.transactions

import com.newmotion.models.Fee
import com.newmotion.server.RedisStore
import com.newmotion.server.http.Responses._
import com.newmotion.service.tracing.Tracing
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponseStatus

import scala.util.{Failure, Success, Try}

class ComputeFeeHandler extends Service[Request, Response] with Tracing with RedisStore {
  override def apply(request: Request): Future[Response] = {
    Try(Fee(request)) match {
      case Success(t) => ???
      case Failure(f) => Future(respond("Errors!", HttpResponseStatus.BAD_REQUEST))
    }
  }
}
