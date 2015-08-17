package com.chargingsystem.core.healthcheck

import com.chargingsystem.server.http.Responses._
import com.chargingsystem.service.tracing.Tracing
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponseStatus

class HealthCheckHandler() extends Service[Request, Response] with Tracing {

  def apply(request: Request): Future[Response] =  {
    withTrace("Healthcheck - #apply", "HealthCheck") {
      Future(respond("It Works! Yay!", HttpResponseStatus.OK))
    }
  }
}
