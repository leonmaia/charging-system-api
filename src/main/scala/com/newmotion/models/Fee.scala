package com.newmotion.models

import com.fasterxml.jackson.databind.JsonMappingException
import com.newmotion.util.JsonSupport
import com.twitter.finagle.http.Request

object Fee extends JsonSupport {
  def apply(request: Request): Fee = {
    try {
      fromJson[Fee](request.getContentString())
    } catch {
      case e: JsonMappingException => {
        throw e.getCause
      }
    }
  }
}
case class Fee(startFee: Double, hourlyFee: Double, feePerKWh: Double, activeStarting: String)

