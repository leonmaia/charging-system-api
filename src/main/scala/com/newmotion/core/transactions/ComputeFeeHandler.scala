package com.newmotion.core.transactions

import com.newmotion.server.RedisStore
import com.newmotion.service.tracing.Tracing
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future

class ComputeFeeHandler extends Service[Request, Response] with Tracing with RedisStore {
  override def apply(request: Request): Future[Response] = ???
}
