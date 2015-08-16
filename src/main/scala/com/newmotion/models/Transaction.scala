package com.newmotion.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonMappingException
import com.newmotion.util.JsonSupport
import com.twitter.finagle.http.Request

object Transaction extends JsonSupport {
  def apply(request: Request): Transaction= {
    try {
      fromJson[Transaction](request.getContentString())
    } catch {
      case e: JsonMappingException => {
        throw e.getCause
      }
    }
  }
}
case class Transaction(@JsonProperty("customerId") id: String, startTime: String, endTime: String, volume: Double)
