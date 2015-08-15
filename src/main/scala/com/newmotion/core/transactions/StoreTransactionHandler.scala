package com.newmotion.core.transactions

import com.newmotion.server.RedisStore
import com.newmotion.server.http.Responses._
import com.newmotion.service.tracing.Tracing
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponseStatus

class StoreTransactionHandler extends Tracing with RedisStore {

  def apply(request: Request): Future[Response] = Future(respond("", HttpResponseStatus.CREATED))
}
